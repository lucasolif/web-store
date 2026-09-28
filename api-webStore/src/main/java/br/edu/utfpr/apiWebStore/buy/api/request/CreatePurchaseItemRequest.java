package br.edu.utfpr.apiWebStore.buy.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePurchaseItemRequest(

        @NotNull(message = "O identificador do produto é obrigatório.")
        @Positive(message = "O identificador do produto deve ser maior que zero.")
        Long productId,

        @NotNull(message = "A quantidade do produto é obrigatória.")
        @DecimalMin(
                value = "0.01",
                message = "A quantidade do produto deve ser maior que zero."
        )
        BigDecimal quantity

) {
}