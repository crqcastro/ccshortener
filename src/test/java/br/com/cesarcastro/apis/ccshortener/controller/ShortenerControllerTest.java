package br.com.cesarcastro.apis.ccshortener.controller;

import br.com.cesarcastro.apis.ccshortener.controller.rest.ShortenerController;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import br.com.cesarcastro.apis.ccshortener.domain.service.IShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import br.com.cesarcastro.apis.ccshortener.test.fixtures.ShortenedURLRequestDTOFixture;
import br.com.cesarcastro.apis.ccshortener.test.fixtures.ShortenedURLResponseDTOFixture;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShortenerControllerTest {
    @Mock
    IShortenerService shortenerService;
    @InjectMocks
    ShortenerController shortenerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should shorten the URL successfully")
    void testShorten() {
        var requestDTO = ShortenedURLRequestDTOFixture.build();
        var responseDTO = ShortenedURLResponseDTOFixture.build();
        var mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");

        when(shortenerService.shorten(any(ShortenedURLRequestDTO.class), anyString())).thenReturn(responseDTO);

        ResponseEntity<ShortenedURLResponseDTO> result = shortenerController.shorten(requestDTO, mockRequest);

        verify(shortenerService, times(1)).shorten(any(ShortenedURLRequestDTO.class), anyString());
        assertEquals(requestDTO.getOriginalUrl(), result.getBody().getOriginalUrl());
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("should resolve target URL and redirect successfully")
    void testResolveTarget() throws IOException {
        when(shortenerService.resolveTargetUrl(anyString())).thenReturn("https://cc.dev.br/AsDfGhJ");
        var mockResponse = mock(HttpServletResponse.class);
        shortenerController.resolveTarget("AsDfGhJ", mockResponse);
        verify(mockResponse, times(1)).sendRedirect("https://cc.dev.br/AsDfGhJ");
        verify(shortenerService, times(1)).resolveTargetUrl("AsDfGhJ");
    }
}

