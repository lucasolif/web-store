package br.edu.utfpr.apipayment.payment.application.pix;

import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PixRepository;
import br.edu.utfpr.apipayment.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CancelPixPaymentService {

    private static PixRepository pixRepository;
    private static PaymentRepository paymentRepository;

    public CancelPixPaymentService(
            PixRepository pixRepository,
            PaymentRepository paymentRepository
    ){
        this.pixRepository = pixRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void deletePixPayment(Long id){

        Pix pixData = pixRepository.findByIdAndStatus(id, PixStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PIX_NOT_FOUND", "Pix expirou ou foi cancelado"
                ));

        Payment payment = paymentRepository.findById(pixData.getPayment().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PAYMENT_NOT_FOUND", "Pagamento não encontrado"
                ));


        payment.setStatus(PaymentStatus.CANCELED);
        payment.setCancellationDate(LocalDate.now());

        pixData.setStatus(PixStatus.CANCELED);
        pixData.setCancellationDate(LocalDate.now());

        paymentRepository.save(payment);
        pixRepository.save(pixData);

    }
}
