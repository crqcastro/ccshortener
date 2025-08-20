package unit.br.com.cesarcastro.apis.ccshortener.test.fixtures;

import br.com.cesarcastro.apis.ccshortener.domain.entities.ShortenedUrlEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.Boolean.TRUE;

public class ShortenedUrlEntityFixture {
    public static ShortenedUrlEntity build() {
        return ShortenedUrlEntity.builder()
                .id(1L)
                .active(TRUE)
                .clicks(0L)
                .code("https://cc.dev.br/abc123")
                .targetUrl("https://www.cesarcastro.com.br")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .createdIp("192.168.1.1")
                .build();
    }

    public static ShortenedUrlEntity buildWithParameter(Map<String, Object> params) {

        var entity = new ShortenedUrlEntity();
        entity.setId(1L);
        entity.setActive(TRUE);
        entity.setClicks(0L);
        entity.setCode("https://cc.dev.br/abc123");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusDays(1));
        entity.setCreatedIp("192.168.1.1");

        params.forEach((key, value) ->
            ReflectionTestUtils.setField(entity, key, value)
        );

        return entity;

    }
}
