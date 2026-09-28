package br.edu.utfpr.apipayment.payment.api.bankslip.response;

import br.edu.utfpr.apipayment.payment.domain.enumeration.BankSlipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BankSlipPaymentResponse(
        String barraCode,
        BigDecimal amount,
        LocalDate dueDate,
        String payerName,
        String payerCpf,
        LocalDate generationDate,
        LocalDate cancellationDate,
        BankSlipStatus status
) {
}
