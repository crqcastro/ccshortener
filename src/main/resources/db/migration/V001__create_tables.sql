CREATE TABLE IF NOT EXISTS url_mapping (
   id BIGSERIAL PRIMARY KEY,
   code VARCHAR(25) NOT NULL UNIQUE,
   target_url TEXT NOT NULL,
   created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
   expires_at TIMESTAMP(3) NOT NULL,
   clicks BIGINT NOT NULL DEFAULT 0,
   active BOOLEAN NOT NULL DEFAULT TRUE,
   created_ip VARCHAR(64)
);

-- Índices adicionais
CREATE INDEX idx_url_mapping_active ON url_mapping (active);
CREATE INDEX idx_url_mapping_expires_at ON url_mapping (expires_at);
