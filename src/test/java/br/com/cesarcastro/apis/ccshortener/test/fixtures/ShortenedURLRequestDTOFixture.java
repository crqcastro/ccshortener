package br.com.cesarcastro.apis.ccshortener.test.fixtures;

import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;

public class ShortenedURLRequestDTOFixture {
    public static ShortenedURLRequestDTO build() {
        return ShortenedURLRequestDTO.builder()
                .originalUrl("https://www.cesarcastro.com.br")
                .build();
    }

    public static ShortenedURLRequestDTO buildWithParameter(String originalUrl) {
        return ShortenedURLRequestDTO.builder()
                .originalUrl(originalUrl)
                .build();
    }
}
