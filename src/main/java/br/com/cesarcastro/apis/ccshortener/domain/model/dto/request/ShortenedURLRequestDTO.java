package br.com.cesarcastro.apis.ccshortener.domain.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Shortened URL Request")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShortenedURLRequestDTO {
    @Schema(description = "Original URL to be shortened.", requiredMode = REQUIRED)
    @Pattern(regexp = "https?://.+", message = "URL must start with http:// or https://")
    private String originalUrl;
}
