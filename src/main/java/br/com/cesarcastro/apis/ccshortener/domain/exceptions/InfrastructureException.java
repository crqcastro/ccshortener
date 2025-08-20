package br.com.cesarcastro.apis.ccshortener.domain.exceptions;

public class InfrastructureException extends RuntimeException {

    public InfrastructureException(String mensagem) {
        super(mensagem);
    }
}
