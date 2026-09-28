package br.edu.utfpr.apiproduto.shared.exception;

public class BusinessException extends RuntimeException {

    private static final String DEFAULT_CODE = "BUSINESS_RULE_VIOLATION";

    private final String code;

    public BusinessException(String message) {
        this(DEFAULT_CODE, message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
