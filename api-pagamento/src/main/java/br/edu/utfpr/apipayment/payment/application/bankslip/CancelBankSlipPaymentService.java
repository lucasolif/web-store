package br.edu.utfpr.apipayment.payment.application.bankslip;

import br.edu.utfpr.apipayment.payment.domain.BankSlip;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.enumeration.BankSlipStatus;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.BankSlipRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CancelBankSlipPaymentService {

    private static BankSlipRepository bankSlipRepository;
    private static PaymentRepository paymentRepository;

    public CancelBankSlipPaymentService(
            BankSlipRepository bankSlipRepository,
            PaymentRepository paymentRepository
    ){
        this.bankSlipRepository = bankSlipRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void deleteBankSlipPayment(Long id){

        BankSlip bankslip = bankSlipRepository.findByIdAndStatus(id, BankSlipStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BANKSLIP_NOT_FOUND", "O boleto venceu ou foi cancelado"
                ));

        Payment payment = paymentRepository.findById(bankslip.getPayment().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PAYMENT_NOT_FOUND", "Pagamento não encontrado"
                ));

        payment.setStatus(PaymentStatus.CANCELED);
        payment.setCancellationDate(LocalDate.now());

        bankslip.setStatus(BankSlipStatus.CANCELED);
        bankslip.setCanceledAt(LocalDate.now());

        paymentRepository.save(payment);
        bankSlipRepository.save(bankslip);

    }
}
