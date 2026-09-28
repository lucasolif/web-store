package br.edu.utfpr.apiDelivery.delivery.api.response;

import java.time.LocalDate;

public record DeliveryResponse(
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