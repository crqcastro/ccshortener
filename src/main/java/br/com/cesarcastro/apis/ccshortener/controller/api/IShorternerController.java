package br.com.cesarcastro.apis.ccshortener.controller.api;

import br.com.cesarcastro.apis.ccshortener.domain.model.dto.request.ShortenedURLRequestDTO;
import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Shortener", description = "Endpoint for URL shortener operation.")
@CrossOrigin
@Validated
@RequestMapping("/v1/shortener")
public interface IShorternerController {
    //    @PreAuthorize("hasRole('ROLE_grupomateus.gmsuite_fin_pix_gerenciador.acao-16')")
    @Operation(summary = "Endpoint para importar um novo certificado digital.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Certificado importado com sucesso.",
                content = @Content(mediaType = APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ShortenedURLResponseDTO.class))
            ),
        @ApiResponse(responseCode = "400", description = "Requisição inválida.", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado.", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro inesperado no servidor.", content = @Content),
        @ApiResponse(responseCode = "503", description = "Serviço não está disponível no momento.", content = @Content)
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Transactional
    ResponseEntity<ShortenedURLResponseDTO> shorten(@RequestBody @Valid ShortenedURLRequestDTO requestDTO);

    @GetMapping
    ResponseEntity<Void> healthCheck();
}
