package br.edu.utfpr.apiWebStore.payment.api.response.pix;

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
