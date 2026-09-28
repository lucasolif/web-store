package br.edu.utfpr.apiWebStore.buy.api.request;

import jakarta.validation.constraints.NotBlank;

public record DeliveryAddressRequest(
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String neighborhood,
        @NotBlank String cep,
        @NotBlank String city,
        @NotBlank String state,
        String complement
) {
}
