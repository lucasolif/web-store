package br.edu.utfpr.apiWebStore.shared.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        @JsonProperty("timestamp") Instant timestamp,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("path") String path,
        @JsonProperty("correlationId") String correlationId,
        @JsonProperty("violations") List<FieldErrorResponse> violations,
        @JsonProperty("retryAfterSeconds") Integer retryAfterSeconds
) {

    public ErrorResponse(
            Instant timestamp,
            String code,
            String message,
            String path,
            String correlationId,
            List<FieldErrorResponse> violations
    ) {
        this(timestamp, code, message, path, correlationId, violations, null);
    }
}
