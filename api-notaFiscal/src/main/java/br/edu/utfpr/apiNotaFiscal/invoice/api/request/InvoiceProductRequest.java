package br.edu.utfpr.apiNotaFiscal.invoice.api.request;

import java.math.BigDecimal;

public record InvoiceProductRequest(
        Long produtoId,
        Long InvoiceId,
        BigDecimal productQuantity,
        BigDecimal productPrice,
        BigDecimal totalAmount
) {
}
