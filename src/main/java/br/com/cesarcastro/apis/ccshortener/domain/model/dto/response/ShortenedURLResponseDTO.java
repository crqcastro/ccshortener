package br.com.cesarcastro.apis.ccshortener.domain.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Shortened URL Response")
@JsonInclude(NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShortenedURLResponseDTO {
    @Schema(description = "Request ID.", requiredMode = REQUIRED)
    private String id;
    @Schema(description = "Original URL.", requiredMode = REQUIRED)
    private String originalUrl;
    @Schema(description = "Shortened URL.", requiredMode = REQUIRED)
    private String shortUrl;
    @Schema(description = "Creation date.", requiredMode = REQUIRED)
    private LocalDateTime createdAt;
    @Schema(description = "Expiration date.", requiredMode = REQUIRED)
    private LocalDateTime expiresAt;
    @Schema(description = "QRCode to shortened link.", requiredMode = REQUIRED)
    private byte[] qrCodeImage;
}
