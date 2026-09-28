package br.edu.utfpr.apiproduto.shared.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;


public record PageResponse<T>(
        @JsonProperty("data") List<T> data,
        @JsonProperty("pagination") PaginationResponse pagination
) {

    public static <T> PageResponse<T> of(Page<T> page) {
        PaginationResponse pagination = new PaginationResponse(
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getSize(),
                page.getTotalElements(),
                page.getNumberOfElements(),
                page.hasNext(),
                page.hasPrevious()
        );

        return new PageResponse<>(page.getContent(), pagination);
    }
}
