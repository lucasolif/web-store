package br.edu.utfpr.apipayment.payment.api.bankslip.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBankSlipPaymentResponse(
        Long id,
        String barraCode,
        BigDecimal valueAmount,
        LocalDate dueDate
) {
}
