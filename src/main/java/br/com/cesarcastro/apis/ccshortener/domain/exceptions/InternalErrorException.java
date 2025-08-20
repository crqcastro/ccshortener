package br.com.cesarcastro.apis.ccshortener.domain.exceptions;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(String message) {
        super(message);
    }
}
