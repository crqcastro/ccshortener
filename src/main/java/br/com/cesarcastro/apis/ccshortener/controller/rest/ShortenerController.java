package br.com.cesarcastro.apis.ccshortener.controller.rest;

import br.com.cesarcastro.apis.ccshortener.controller.api.IShorternerController;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import br.com.cesarcastro.apis.ccshortener.domain.service.IShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ShortenerController implements IShorternerController {
    private final IShortenerService shortenerService;

    @Override
    public ResponseEntity<ShortenedURLResponseDTO> shorten(ShortenedURLRequestDTO requestDTO) {
        return ResponseEntity.ok(shortenerService.shorten(requestDTO));
    }

    @Override
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
