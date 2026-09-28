package br.edu.utfpr.apipayment.shared.exception;

public class UnauthorizedException extends RuntimeException {

    private static final String DEFAULT_CODE = "UNAUTHORIZED";

    private final String code;

    public UnauthorizedException(String message) {
        this(DEFAULT_CODE, message);
    }

    public UnauthorizedException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
