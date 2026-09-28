package br.edu.utfpr.apiNotaFiscal.shared.response;

import com.fasterxml.jackson.annotation.JsonProperty;


public record ApiResponse<T>(@JsonProperty("data") T data) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data);
    }
}
