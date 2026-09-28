package br.edu.utfpr.apipayment.shared.exception;

public class ValidationException extends RuntimeException {

    private static final String DEFAULT_CODE = "VALIDATION_ERROR";

    private final String code;

    public ValidationException(String message) {
        this(DEFAULT_CODE, message);
    }

    public ValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
