package br.edu.utfpr.apiWebStore.shared.exception;

import br.edu.utfpr.apiWebStore.shared.exception.response.FieldErrorResponse;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public class UpstreamApiException extends RuntimeException {

    private final HttpStatusCode status;
    private final String code;
    private final List<FieldErrorResponse> violations;
    private final Integer retryAfterSeconds;

    public UpstreamApiException(
            HttpStatusCode status,
            String code,
            String message,
            List<FieldErrorResponse> violations,
            Integer retryAfterSeconds,
            Throwable cause
    ) {
        super(message, cause);
        this.status = status;
        this.code = code;
        this.violations = violations == null ? List.of() : List.copyOf(violations);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<FieldErrorResponse> getViolations() {
        return violations;
    }

    public Integer getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
