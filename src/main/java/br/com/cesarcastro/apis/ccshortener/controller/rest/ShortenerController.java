package br.com.cesarcastro.apis.ccshortener.controller.rest;

import br.com.cesarcastro.apis.ccshortener.controller.api.IShorternerController;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import br.com.cesarcastro.apis.ccshortener.domain.service.IShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@RestController
public class ShortenerController implements IShorternerController {
    private final IShortenerService shortenerService;

    @Override
    public ResponseEntity<ShortenedURLResponseDTO> shorten(ShortenedURLRequestDTO requestDTO,
                                                           HttpServletRequest req) {
        var shorten = shortenerService.shorten(requestDTO, req.getRemoteAddr());
        return ResponseEntity.created(URI.create(shorten.getShortUrl())).body(shorten);
    }

    @Override
    public void resolveTarget(String code, HttpServletResponse response) throws IOException {
        var targetUrl = shortenerService.resolveTargetUrl(code);
        response.sendRedirect(targetUrl);
    }

}
