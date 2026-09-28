package br.edu.utfpr.apiWebStore.email.api.request;

import java.math.BigDecimal;

public record InvoiceProductRequest(
        Long id,
        Long productId,
        BigDecimal productQuantity,
        BigDecimal productPrice,
        BigDecimal totalAmount
) {
}
