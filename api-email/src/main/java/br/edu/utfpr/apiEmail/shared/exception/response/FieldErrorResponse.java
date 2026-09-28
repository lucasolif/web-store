package br.edu.utfpr.apiEmail.shared.exception.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FieldErrorResponse(
        @JsonProperty("field") String field,
        @JsonProperty("message") String message
) {
}
