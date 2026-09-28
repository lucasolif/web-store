package br.edu.utfpr.apiWebStore.shared.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PageResponse<T>(
        @JsonProperty("data") List<T> data,
        @JsonProperty("pagination") PaginationResponse pagination
) {
}
