package br.edu.utfpr.apiWebStore.buy.api.request;

public record CreateDeliveryAddressRequest(
        String street,
        String neighborhood,
        String number,
        String cep,
        String complement,
        String city,
        String state
) {}
