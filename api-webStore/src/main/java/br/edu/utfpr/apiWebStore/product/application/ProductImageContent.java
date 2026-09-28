package br.edu.utfpr.apiWebStore.product.application;

import org.springframework.http.MediaType;

public record ProductImageContent(
        byte[] content,
        MediaType mediaType,
        String cacheControl
) {
}
