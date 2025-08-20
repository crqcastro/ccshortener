package unit.br.com.cesarcastro.apis.ccshortener.domain.service;

import br.com.cesarcastro.apis.ccshortener.domain.entities.ShortenedUrlEntity;
import br.com.cesarcastro.apis.ccshortener.domain.mapper.ShortenedMapper;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import br.com.cesarcastro.apis.ccshortener.domain.repository.IShortenedUrlRepository;
import br.com.cesarcastro.apis.ccshortener.domain.service.impl.ShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import unit.br.com.cesarcastro.apis.ccshortener.test.fixtures.ShortenedURLRequestDTOFixture;
import unit.br.com.cesarcastro.apis.ccshortener.test.fixtures.ShortenedURLResponseDTOFixture;
import unit.br.com.cesarcastro.apis.ccshortener.test.fixtures.ShortenedUrlEntityFixture;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class ShortenerServiceTest {
    @Mock
    IShortenedUrlRepository shortenedUrlRepository;
    @Mock
    ShortenedMapper mapper;
    @InjectMocks
    ShortenerService shortenerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            Field lengthField = ShortenerService.class.getDeclaredField("length");
            lengthField.setAccessible(true);
            lengthField.set(shortenerService, 7);

            Field freeDays = ShortenerService.class.getDeclaredField("freeDays");
            freeDays.setAccessible(true);
            freeDays.set(shortenerService, 15);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("You should test URL shortening")
    void testShorten() {
        when(shortenedUrlRepository.findByTargetUrl(anyString())).thenReturn(Optional.empty());
        when(shortenedUrlRepository.existsByCode(anyString())).thenReturn(FALSE);
        var entity = ShortenedUrlEntityFixture.build();
        when(shortenedUrlRepository.save(any(ShortenedUrlEntity.class))).thenReturn(entity);
        Map<String, Object> entityParams = new HashMap<>();
        entityParams.put("targetUrl", "https://www.google.com");
        when(mapper.convertToEntity(any(ShortenedURLRequestDTO.class)))
                .thenReturn(ShortenedUrlEntityFixture.buildWithParameter(entityParams));
        Map<String, Object> dtoParams = new HashMap<>();
        dtoParams.put("originalUrl", "https://www.google.com");
        var shortenedResponse = ShortenedURLResponseDTOFixture.buildWithParameter(dtoParams);
        when(mapper.toResponseDTO(any(ShortenedUrlEntity.class))).thenReturn(shortenedResponse);

        ShortenedURLResponseDTO result = shortenerService
                .shorten(ShortenedURLRequestDTOFixture.buildWithParameter("https://www.google.com"), "0.0.0.0.0.1");

        assertEquals(entityParams.get("targetUrl"), result.getOriginalUrl());
    }

    @Test
    @DisplayName("You should test URL resolution")
    void testResolveTargetUrl() {
        var entity = ShortenedUrlEntityFixture.build();

        when(shortenedUrlRepository.findByCodeAndActive(anyString(), anyBoolean())).thenReturn(Optional.of(entity));
        when(shortenedUrlRepository.save(any(ShortenedUrlEntity.class))).thenReturn(entity);

        String result = shortenerService.resolveTargetUrl("https://cc.dev.br/XyZkAs");
        assertEquals("https://www.cesarcastro.com.br", result);
    }
}
