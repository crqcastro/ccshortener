package br.com.cesarcastro.apis.ccshortener.domain.service.impl;

import br.com.cesarcastro.apis.ccshortener.domain.entities.ShortenedUrlEntity;
import br.com.cesarcastro.apis.ccshortener.domain.exceptions.ResourceNotFoundException;
import br.com.cesarcastro.apis.ccshortener.domain.mapper.ShortenedMapper;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import br.com.cesarcastro.apis.ccshortener.domain.repository.IShortenedUrlRepository;
import br.com.cesarcastro.apis.ccshortener.domain.service.IShortenerService;
import br.com.cesarcastro.apis.ccshortener.util.Base62;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class ShortenerService implements IShortenerService {

    private final IShortenedUrlRepository shortenedUrlRepository;
    private final ShortenedMapper mapper;

    @Value("${ccshortener.code.base-url}")
    private String baseUrl;

    @Value("${ccshortener.code.length}")
    private Integer length;

    @Value("${ccshortener.code.free-days}")
    private Integer freeDays;

    @Override
    @Transactional
    public ShortenedURLResponseDTO shorten(ShortenedURLRequestDTO requestDTO, String ip) {
        getPreExisting(requestDTO.getOriginalUrl()).ifPresent(mapper::toResponseDTO);

        ShortenedUrlEntity entity = mapper.convertToEntity(requestDTO);
        entity.setCode(shortenUrl());
        entity.setExpiresAt(LocalDateTime.now().plusDays(freeDays));
        entity.setActive(TRUE);
        entity.setCreatedIp(ip);

        shortenedUrlRepository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public String resolveTargetUrl(String code) {
        var target = shortenedUrlRepository.findByCodeAndActive(baseUrl + code, TRUE);
        if (target.isPresent()) {
            var entity = target.get();
            entity.setClicks(entity.getClicks() + 1);
            shortenedUrlRepository.save(entity);
            return entity.getTargetUrl();
        }
        throw new ResourceNotFoundException("Invalid code or URL not found");
    }


    private Optional<ShortenedUrlEntity> getPreExisting(String originalUrl) {
        return shortenedUrlRepository.findByTargetUrl(originalUrl);
    }

    private String shortenUrl() {
        return String.format("%s%s", baseUrl, generateUniqueCode());
    }

    private String generateUniqueCode() {
        for (int attempts = 0; attempts < 20; attempts++) {
            var c = Base62.random(length);
            if (!shortenedUrlRepository.existsByCode(c)) {
                return c;
            }
        }
        throw new IllegalStateException("Falha ao gerar código único");
    }
}
