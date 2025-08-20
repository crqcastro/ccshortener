package br.com.cesarcastro.apis.ccshortener.domain.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String path;
    private final String error;

    public BusinessException(HttpStatus httpStatus, String message, String path, String error) {
        super(message);
        this.httpStatus = httpStatus;
        this.path = path;
        this.error = error;
    }

    public BusinessException(HttpStatus httpStatus, String s) {
        super(s);
        this.httpStatus = httpStatus;
        this.path = "";
        this.error = httpStatus.name();
    }
}
