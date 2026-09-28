package br.edu.utfpr.apipayment.payment.api.card.response;

import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentMethod;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardPaymentResponse(
        Long id,
        PaymentMethod paymentMethod,
        String buyerName,
        String buyerCpf,
        BigDecimal totalAmount,
        PaymentStatus status,
        LocalDate paymentData,
        LocalDate cancellationDate,
        String cardNumber,
        String cardholderName,
        String cardholderCpf
) {}
