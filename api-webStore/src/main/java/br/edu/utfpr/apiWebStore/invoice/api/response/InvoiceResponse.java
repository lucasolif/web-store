package br.edu.utfpr.apiWebStore.invoice.api.response;

import br.edu.utfpr.apiWebStore.invoice.domain.enumeration.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;

public record InvoiceResponse(
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
        InvoiceStatus status,
        List<InvoiceProductResponse> products
) {}