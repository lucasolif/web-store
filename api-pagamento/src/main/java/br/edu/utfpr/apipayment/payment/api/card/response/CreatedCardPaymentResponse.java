package br.edu.utfpr.apipayment.payment.api.card.response;

import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;

import java.time.LocalDate;

public record CreatedCardPaymentResponse(
        Long id,
        LocalDate createdAt,
        PaymentStatus status
) {}
