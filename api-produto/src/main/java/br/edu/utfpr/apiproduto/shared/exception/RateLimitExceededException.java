package br.edu.utfpr.apiproduto.shared.exception;

public class RateLimitExceededException extends RuntimeException {

    private static final String DEFAULT_CODE = "RATE_LIMIT_EXCEEDED";

    private final String code;
    private final int retryAfterSeconds;

    public RateLimitExceededException(String message, int retryAfterSeconds) {
        this(DEFAULT_CODE, message, retryAfterSeconds);
    }

    public RateLimitExceededException(
            String code,
            String message,
            int retryAfterSeconds
    ) {
        super(message);
        this.code = code;
        this.retryAfterSeconds = Math.max(0, retryAfterSeconds);
    }

    public String getCode() {
        return code;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
