package br.edu.utfpr.apiWebStore.invoice.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record InvoiceProductRequest(
        @NotNull(message = "O identificador do produto é obrigatório.")
        @Positive(message = "O identificador do produto deve ser maior que zero.")
        Long produtoId,
        Long InvoiceId,
        @NotNull(message = "A quantidade do produto é obrigatória.")
        @DecimalMin(value = "0.01", message = "A quantidade do produto deve ser maior que zero.")
        BigDecimal productQuantity,
        @NotNull(message = "O preço do produto é obrigatório.")
        @DecimalMin(value = "0.00", inclusive = false, message = "O preço do produto deve ser maior que zero.")
        BigDecimal productPrice,
        @NotNull(message = "O valor total do produto é obrigatório.")
        @DecimalMin(value = "0.00", inclusive = false, message = "O valor total do produto deve ser maior que zero.")
        BigDecimal totalAmount
) {
}
