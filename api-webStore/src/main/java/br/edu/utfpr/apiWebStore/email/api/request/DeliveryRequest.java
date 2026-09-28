package br.edu.utfpr.apiWebStore.email.api.request;

import java.time.LocalDate;

public record DeliveryRequest(
        Long id,
        String trackingCode,
        LocalDate deliveryPrevision,
        String customer,
        String customerCpf,
        String customerEmail,
        String customerAddressStreet,
        String customerAddressNumber,
        String customerAddressNeighborhood,
        String customerAddressCep,
        String customerAddressCity,
        String customerAddressState,
        String customerAddressComplement
) {
}
