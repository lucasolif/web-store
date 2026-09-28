package br.edu.utfpr.apiNotaFiscal.invoice.api.response;

import java.math.BigDecimal;

public record InvoiceProductResponse(
        Long id,
        Long productId,
        BigDecimal productQuantity,
        BigDecimal productPrice,
        BigDecimal totalAmount
) {}