package br.com.cesarcastro.apis.ccshortener.domain.model.dto.error;

import java.io.Serializable;

public record FieldErrorDTO(String fieldName, String reason) implements Serializable {
}
