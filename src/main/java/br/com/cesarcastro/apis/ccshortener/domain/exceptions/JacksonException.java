package br.com.cesarcastro.apis.ccshortener.domain.exceptions;

public class JacksonException extends RuntimeException {

    public JacksonException(String mensagem) {
        super(mensagem);
    }
}
