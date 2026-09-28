package br.edu.utfpr.apiEmail.shared.exception.web;

public final class RequestAttributes {

    public static final String CORRELATION_ID = "productApi.correlationId";
    public static final String ERROR_CODE = "productApi.errorCode";

    private RequestAttributes() {
        throw new IllegalStateException("Esta classe não pode ser instanciada.");
    }
}
