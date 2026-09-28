package br.edu.utfpr.apiWebStore.shared.exception.web;

public final class RequestAttributes {

    public static final String CORRELATION_ID = "webStoreApi.correlationId";
    public static final String ERROR_CODE = "webStoreApi.errorCode";

    private RequestAttributes() {
        throw new IllegalStateException("Esta classe não pode ser instanciada.");
    }
}
