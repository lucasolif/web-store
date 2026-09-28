package br.edu.utfpr.apipayment.payment.application.card;

import br.edu.utfpr.apipayment.payment.api.card.request.CreateCardPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.card.response.CreatedCardPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.Card;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.infrastructure.mapper.CardPaymentMapper;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.CardRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CreateCardPaymentService {

    private static CardRepository cardRepository;
    private static PaymentRepository paymentRepository;
    private static CardPaymentMapper cardPaymentMapper;

    public CreateCardPaymentService(
            CardRepository cardRepository,
            PaymentRepository paymentRepository,
            CardPaymentMapper cardPaymentMapper
    ){
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentMapper = cardPaymentMapper;
    }

    @Transactional
    public CreatedCardPaymentResponse createdCardPayment(CreateCardPaymentRequest cardRequest){

        Payment payment = cardPaymentMapper.toPayment(cardRequest);
        payment.setCreatedAt(LocalDate.now());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletionDate(LocalDate.now());
        payment.setPurchaseId(cardRequest.purchaseId());
        Payment paymentCreated = paymentRepository.save(payment);

        Card cardPayment = cardPaymentMapper.toCard(cardRequest);
        cardPayment.setPayment(paymentCreated);
        cardRepository.save(cardPayment);

        return new CreatedCardPaymentResponse(
                paymentCreated.getId(),
                paymentCreated.getCreatedAt(),
                paymentCreated.getStatus()
        );
    }
}
