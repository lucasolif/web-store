package br.edu.utfpr.apipayment.payment.application.card;

import br.edu.utfpr.apipayment.payment.domain.Card;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.infrastructure.mapper.CardPaymentMapper;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.CardRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DeleteCardPaymentService {

    private static CardRepository cardRepository;
    private static PaymentRepository paymentRepository;

    public DeleteCardPaymentService(
            CardRepository cardRepository,
            PaymentRepository paymentRepository
    ){
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void deleteCardPayment(Long id){

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                "PAYMENT_NOT_FOUND", "Pagamento não encontrado: " + id
        ));

        Card cardData = cardRepository.findByPaymentId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                 "CARD_NOT_FOUND", "Dados do cartão não encontrado"
        ));

        payment.setStatus(PaymentStatus.CANCELED);
        payment.setCancellationDate(LocalDate.now());

        paymentRepository.save(payment);
        cardRepository.delete(cardData);

    }

}
