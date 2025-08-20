package br.com.cesarcastro.apis.ccshortener.domain.repository;

import br.com.cesarcastro.apis.ccshortener.domain.entities.ShortenedUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IShortenedUrlRepository extends JpaRepository<ShortenedUrlEntity, Long>,
        JpaSpecificationExecutor<ShortenedUrlEntity> {
    Optional<ShortenedUrlEntity> findByTargetUrl(String originalUrl);

    boolean existsByCode(String code);
}
