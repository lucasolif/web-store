package br.edu.utfpr.apiWebStore.payment.api.response.card;

import br.edu.utfpr.apiWebStore.payment.domain.enumeration.PaymentStatus;

import java.time.LocalDate;

public record CreateCardPaymentResponse(
        Long id,
        LocalDate createdAt,
        PaymentStatus status
) {
}
