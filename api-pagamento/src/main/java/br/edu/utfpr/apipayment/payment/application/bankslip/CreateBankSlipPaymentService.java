package br.edu.utfpr.apipayment.payment.application.bankslip;


import br.edu.utfpr.apipayment.payment.api.bankslip.request.CreateBankSlipPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.bankslip.response.CreateBankSlipPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.BankSlip;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.infrastructure.mapper.BankSlipPaymentMapper;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.BankSlipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;

@Service
public class CreateBankSlipPaymentService {
    private static BankSlipRepository bankSlipRepository;
    private static PaymentRepository paymentRepository;
    private static BankSlipPaymentMapper bankSlipPaymentMapper;

    public CreateBankSlipPaymentService(
            BankSlipRepository bankSlipRepository,
            PaymentRepository paymentRepository,
            BankSlipPaymentMapper bankSlipPaymentMapper
    ){
        this.bankSlipRepository = bankSlipRepository;
        this.paymentRepository = paymentRepository;
        this.bankSlipPaymentMapper = bankSlipPaymentMapper;
    }

    @Transactional
    public CreateBankSlipPaymentResponse createBankSlipPayment(CreateBankSlipPaymentRequest bankSlipRequest){

        Payment payment = bankSlipPaymentMapper.toPayment(bankSlipRequest);
        payment.setCreatedAt(LocalDate.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPurchaseId(bankSlipRequest.purchaseId());

        Payment paymentCreated = paymentRepository.save(payment);

        BankSlip BankSlipPayment = bankSlipPaymentMapper.toBankSlip(bankSlipRequest);
        BankSlipPayment.setPayment(paymentCreated);
        BankSlipPayment.setDueDate(LocalDate.now().plusDays(3));
        BankSlipPayment.setBarcode(this.gerarCodigoBankSlip());

        BankSlip BankSlipPaymentCreated = bankSlipRepository.save(BankSlipPayment);

        return new CreateBankSlipPaymentResponse(
                BankSlipPaymentCreated.getId(),
                BankSlipPaymentCreated.getBarcode(),
                BankSlipPaymentCreated.getAmount(),
                BankSlipPaymentCreated.getDueDate()
        );

    }

    private String gerarCodigoBankSlip() {
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder(44);

        for (int i = 0; i < 44; i++) {
            result.append(random.nextInt(10));
        }

        return result.toString();
    }
}
