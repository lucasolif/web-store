package br.edu.utfpr.apipayment.payment.infrastructure.mapper;

import br.edu.utfpr.apipayment.payment.api.card.request.CreateCardPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.card.response.CardPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.Card;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentMapper {

    public Payment toPayment(CreateCardPaymentRequest cardRequest){
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setBuyerName(cardRequest.buyerName());
        payment.setBuyerCpf(cardRequest.buyerCpf());
        payment.setTotalAmount(cardRequest.totalAmount());

        return payment;
    }

    public Card toCard(CreateCardPaymentRequest cardRequest){
        Card cardPayment = new Card();
        cardPayment.setCardNumber(cardRequest.cardNumber());
        cardPayment.setSecurityCode(cardRequest.securityCode());
        cardPayment.setCardholderCpf(cardRequest.cardholderCpf());
        cardPayment.setCardholderName(cardRequest.cardholderName());
        cardPayment.setExpirationDate(cardRequest.expirationDate());

        return cardPayment;
    }

    public CardPaymentResponse toResponse(Card cardPayment){

        Payment payment = cardPayment.getPayment();

        return new CardPaymentResponse(
                payment.getId(),
                payment.getPaymentMethod(),
                payment.getBuyerName(),
                payment.getBuyerCpf(),
                payment.getTotalAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getCancellationDate(),
                cardPayment.getCardNumber(),
                cardPayment.getCardholderName(),
                cardPayment.getCardholderCpf()
        );
    }
}
