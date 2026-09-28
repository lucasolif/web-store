package br.edu.utfpr.apipayment.shared.exception.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FieldErrorResponse(
        @JsonProperty("field") String field,
        @JsonProperty("message") String message
) {
}
