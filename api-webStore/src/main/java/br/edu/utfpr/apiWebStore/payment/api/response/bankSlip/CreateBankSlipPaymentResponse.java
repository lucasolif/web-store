package br.edu.utfpr.apiWebStore.payment.api.response.bankSlip;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBankSlipPaymentResponse(
        Long id,
        @JsonAlias("barraCode") String barcode,
        @JsonAlias("valueAmount") BigDecimal amount,
        LocalDate dueDate
) {
}
