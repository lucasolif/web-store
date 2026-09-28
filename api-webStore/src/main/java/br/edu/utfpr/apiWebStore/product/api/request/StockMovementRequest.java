package br.edu.utfpr.apiWebStore.product.api.request;

import br.edu.utfpr.apiWebStore.product.domain.enumeration.StockMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StockMovementRequest(
        @NotNull(message = "A quantidade é obrigatória")
        @DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero")
        BigDecimal quantity,

        @NotNull(message = "A operação é obrigatória")
        StockMovementType operation
) {
}
