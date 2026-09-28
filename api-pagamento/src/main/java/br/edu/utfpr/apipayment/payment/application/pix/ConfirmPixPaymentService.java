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
public class ConfirmPixPaymentService {

    private static PixRepository pixRepository;
    private static PaymentRepository paymentRepository;

    public ConfirmPixPaymentService(
            PixRepository pixRepository,
            PaymentRepository paymentRepository
    ){
        this.pixRepository = pixRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void confirmPixPayment(Long id){
        Pix pix = pixRepository.findByIdAndStatus(id, PixStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PIX_NOT_FOUND", "Pix não encontrado ou expirado"
                ));

        Payment payment = paymentRepository.findById(pix.getPayment().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                "PAYMENT_NOT_FOUND", "Pagamento não encontrado ou expirado"
        ));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletionDate(LocalDate.now());

        pix.setStatus(PixStatus.PAID);
        pix.setPaymentDate(LocalDate.now());

        paymentRepository.save(payment);
        pixRepository.save(pix);

    }

}
