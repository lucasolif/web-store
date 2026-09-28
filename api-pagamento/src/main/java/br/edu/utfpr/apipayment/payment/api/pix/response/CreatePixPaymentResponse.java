package br.edu.utfpr.apipayment.payment.api.pix.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreatePixPaymentResponse(
        Long id,
        String qrCode,
        LocalDate expirationDate,
        LocalTime expirationTime,
        BigDecimal totalAmount
) {
}
