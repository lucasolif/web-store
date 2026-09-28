package br.edu.utfpr.apipayment.payment.application.bankslip;

import br.edu.utfpr.apipayment.payment.domain.BankSlip;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.domain.enumeration.BankSlipStatus;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.BankSlipRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PixRepository;
import br.edu.utfpr.apipayment.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ConfirmBankSlipPaymentService {

    private static BankSlipRepository bankSlipRepository;
    private static PaymentRepository paymentRepository;

    public ConfirmBankSlipPaymentService(
            BankSlipRepository bankSlipRepository,
            PaymentRepository paymentRepository
    ){
        this.bankSlipRepository = bankSlipRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void confirmBankSlipPayment(Long id){

        BankSlip bankSlip = bankSlipRepository.findByIdAndStatus(id, BankSlipStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PIX_NOT_FOUND", "Boleto não encontrado ou expirado"
                ));

        Payment payment = paymentRepository.findById(bankSlip.getPayment().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PAYMENT_NOT_FOUND", "Pagamento não encontrado ou expirado"
                ));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletionDate(LocalDate.now());

        bankSlip.setStatus(BankSlipStatus.PAID);
        bankSlip.setPaymentDate(LocalDate.now());

        paymentRepository.save(payment);
        bankSlipRepository.save(bankSlip);

    }
}
