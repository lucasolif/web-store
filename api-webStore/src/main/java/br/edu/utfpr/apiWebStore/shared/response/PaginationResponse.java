package br.edu.utfpr.apiWebStore.shared.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaginationResponse(
        @JsonProperty("currentPage") int currentPage,
        @JsonProperty("totalPages") int totalPages,
        @JsonProperty("pageSize") int pageSize,
        @JsonProperty("totalRecords") long totalRecords,
        @JsonProperty("recordsOnPage") int recordsOnPage,
        @JsonProperty("hasNextPage") boolean hasNextPage,
        @JsonProperty("hasPreviousPage") boolean hasPreviousPage
) {
}
