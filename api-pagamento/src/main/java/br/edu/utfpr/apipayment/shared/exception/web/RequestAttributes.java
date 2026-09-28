package br.edu.utfpr.apipayment.shared.exception.web;

public final class RequestAttributes {

    public static final String CORRELATION_ID = "paymentApi.correlationId";
    public static final String ERROR_CODE = "paymentApi.errorCode";

    private RequestAttributes() {
        throw new IllegalStateException("Esta classe não pode ser instanciada.");
    }
}
