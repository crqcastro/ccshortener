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

@RequiredArgsConstructor
@RestController
public class ShortenerController implements IShorternerController {
    private final IShortenerService shortenerService;

    @Override
    public ResponseEntity<ShortenedURLResponseDTO> shorten(ShortenedURLRequestDTO requestDTO,
                                                           HttpServletRequest req) {
        return ResponseEntity.ok(shortenerService.shorten(requestDTO, req.getRemoteAddr()));
    }

    @Override
    public void resolveTarget(String code, HttpServletResponse response) throws IOException {
        var targetUrl = shortenerService.resolveTargetUrl(code);
        response.sendRedirect(targetUrl);
    }

}
