package br.edu.utfpr.apiNotaFiscal.invoice.api.request;

import br.edu.utfpr.apiNotaFiscal.invoice.domain.enumeration.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
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
        List<InvoiceProductRequest> products
) {}