package br.edu.utfpr.apipayment.payment.application.pix;

import br.edu.utfpr.apipayment.payment.api.card.response.CardPaymentResponse;
import br.edu.utfpr.apipayment.payment.api.pix.response.PixPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.Card;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.infrastructure.mapper.PixPaymentMapper;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PixRepository;
import br.edu.utfpr.apipayment.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apipayment.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchPixPaymentService {

    private static PixRepository pixRepository;
    private static PixPaymentMapper pixPaymentMapper;

    public SearchPixPaymentService(
            PixRepository pixRepository,
            PixPaymentMapper pixPaymentMapper
    ){
        this.pixRepository = pixRepository;
        this.pixPaymentMapper = pixPaymentMapper;
    }

    @Transactional(readOnly = true)
    public PixPaymentResponse findById(Long id){
        Pix pixPayment = pixRepository.findByPaymentId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PIX_NOT_FOUND", "Pix não encontrado: " + id
                ));

        return pixPaymentMapper.toResponse(pixPayment);
    }

    @Transactional(readOnly = true)
    public PageResponse<PixPaymentResponse> findAll(Pageable pageable) {
        return PageResponse.of(pixRepository.findAll(pageable).map(pixPaymentMapper::toResponse));
    }

}
