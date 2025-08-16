package br.com.cesarcastro.apis.ccshortener.domain.service;

import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;

public interface IShortenerService {
    ShortenedURLResponseDTO shorten(ShortenedURLRequestDTO requestDTO);
}
