package br.com.cesarcastro.apis.ccshortener.domain.exceptions;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}
