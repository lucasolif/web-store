package br.edu.utfpr.apipayment.payment.application.card;

import br.edu.utfpr.apipayment.payment.api.card.response.CardPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.Card;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.infrastructure.mapper.CardPaymentMapper;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.CardRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apipayment.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchCardPaymentService {

    private static CardRepository cardRepository;
    private static CardPaymentMapper cardPaymentMapper;

    public SearchCardPaymentService(CardRepository cardRepository, CardPaymentMapper cardPaymentMapper){
        this.cardRepository = cardRepository;
        this.cardPaymentMapper = cardPaymentMapper;
    }

    @Transactional(readOnly = true)
    public CardPaymentResponse findById(Long id){
        Card cardPayment = cardRepository.findByPaymentId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                "PAYMENT_NOT_FOUND", "Pagamento não encontrado: " + id
        ));

        return cardPaymentMapper.toResponse(cardPayment);
    }

    @Transactional(readOnly = true)
    public PageResponse<CardPaymentResponse> findAll(Pageable pageable) {
        return PageResponse.of(cardRepository.findAll(pageable).map(cardPaymentMapper::toResponse));
    }

}
