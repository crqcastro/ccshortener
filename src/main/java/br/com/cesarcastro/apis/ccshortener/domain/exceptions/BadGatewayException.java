package br.com.cesarcastro.apis.ccshortener.domain.exceptions;

public class BadGatewayException extends RuntimeException {
    public BadGatewayException(String message) {
        super(message);
    }
}
