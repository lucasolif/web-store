package br.edu.utfpr.apipayment.payment.api.pix.response;

import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record PixPaymentResponse(
        String qrCode,
        BigDecimal amount,
        LocalDate expirationDate,
        LocalTime expirationTime,
        String payerName,
        String payerCpf,
        LocalDate generationDate,
        LocalDate cancellationDate,
        PixStatus status
) {
}
