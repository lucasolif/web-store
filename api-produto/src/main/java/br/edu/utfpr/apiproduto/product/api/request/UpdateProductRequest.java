package br.edu.utfpr.apiproduto.product.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String name,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String description,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        @Digits(integer = 13, fraction = 2, message = "O preço deve ter no máximo 13 inteiros e 2 decimais")
        BigDecimal price
) {
}
