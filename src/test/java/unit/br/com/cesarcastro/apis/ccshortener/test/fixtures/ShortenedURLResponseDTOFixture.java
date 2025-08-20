package unit.br.com.cesarcastro.apis.ccshortener.test.fixtures;

import br.com.cesarcastro.apis.ccshortener.domain.model.dto.response.ShortenedURLResponseDTO;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

public class ShortenedURLResponseDTOFixture {
    public static ShortenedURLResponseDTO build() {
        return ShortenedURLResponseDTO.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1L))
                .originalUrl("https://www.cesarcastro.com.br")
                .shortUrl("https://cc.dev.br/XyZkAs")
                .build();
    }

    public static ShortenedURLResponseDTO buildWithParameter(Map<String, Object> params) {
        var dto = build();
        params.forEach((key, value) ->
                ReflectionTestUtils.setField(dto, key, value)
        );
        return dto;
    }
}
