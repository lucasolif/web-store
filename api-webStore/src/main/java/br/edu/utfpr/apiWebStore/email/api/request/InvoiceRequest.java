package br.edu.utfpr.apiWebStore.email.api.request;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
        Long id,
        String customer,
        String customerCpf,
        String customerEmail,
        String customerCelphone,
        String customerAddressStreet,
        String customerAddressNumber,
        String customerAddressNeighborhood,
        String customerAddressCep,
        String customerAddressCity,
        String customerAddressState,
        String customerAddressComplement,
        Long purchaseId,
        LocalDate createdAt,
        LocalDate canceledAt,
        String status,
        List<InvoiceProductRequest> products
) {
}
