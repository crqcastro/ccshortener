package br.com.cesarcastro.apis.ccshortener.domain.model.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Builder
@JsonInclude(NON_EMPTY)
public class ErrorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeStamp;

    private int status;

    private String error;

    private Integer pixErrorCode;

    private String message;

    private String path;

    private List<FieldErrorDTO> violations;

    public void addViolation(String fieldName, String reason) {
        violations.add(new FieldErrorDTO(fieldName, reason));
    }
}
