package br.edu.utfpr.apipayment.payment.infrastructure.mapper;

import br.edu.utfpr.apipayment.payment.api.card.request.CreateCardPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.pix.request.CreatePixPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.pix.response.PixPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentMethod;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class PixPaymentMapper {

    public Payment toPayment(CreatePixPaymentRequest pixRequest){
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.PIX);
        payment.setBuyerName(pixRequest.payerName());
        payment.setBuyerCpf(pixRequest.payerCpf());
        payment.setTotalAmount(pixRequest.totalAmount());

        return payment;
    }

    public Pix toPix(CreatePixPaymentRequest pixRequest){
        Pix pix = new Pix();

        pix.setAmount(pixRequest.totalAmount());
        pix.setExpirationDate(LocalDate.now());
        pix.setExpirationTime(LocalTime.now().plusHours(1));
        pix.setPayerName(pixRequest.payerName());
        pix.setPayerCpf(pixRequest.payerCpf());
        pix.setGenerationDate(LocalDate.now());
        pix.setStatus(PixStatus.PENDING);

        return pix;
    }

    public PixPaymentResponse toResponse(Pix pixPayment) {
        return new PixPaymentResponse(
                pixPayment.getQrCode(),
                pixPayment.getAmount(),
                pixPayment.getExpirationDate(),
                pixPayment.getExpirationTime(),
                pixPayment.getPayerName(),
                pixPayment.getPayerCpf(),
                pixPayment.getGenerationDate(),
                pixPayment.getCancellationDate(),
                pixPayment.getStatus()
        );
    }
}
