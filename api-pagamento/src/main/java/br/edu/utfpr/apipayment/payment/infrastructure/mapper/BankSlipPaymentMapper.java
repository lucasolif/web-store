package br.edu.utfpr.apipayment.payment.infrastructure.mapper;

import br.edu.utfpr.apipayment.payment.api.bankslip.request.CreateBankSlipPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.bankslip.response.BankSlipPaymentResponse;
import br.edu.utfpr.apipayment.payment.api.pix.request.CreatePixPaymentRequest;
import br.edu.utfpr.apipayment.payment.domain.BankSlip;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.domain.enumeration.BankSlipStatus;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentMethod;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class BankSlipPaymentMapper {

    public Payment toPayment(CreateBankSlipPaymentRequest bankSlipRquest){
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.BANK_SLIP);
        payment.setBuyerName(bankSlipRquest.payerName());
        payment.setBuyerCpf(bankSlipRquest.payerCpf());
        payment.setTotalAmount(bankSlipRquest.totalAmount());

        return payment;
    }

    public BankSlip toBankSlip(CreateBankSlipPaymentRequest bankSlipRquest){
        BankSlip bankSlip = new BankSlip();

        bankSlip.setAmount(bankSlipRquest.totalAmount());
        bankSlip.setPayerName(bankSlipRquest.payerName());
        bankSlip.setPayerCpf(bankSlipRquest.payerCpf());
        bankSlip.setGeneratedAt(LocalDate.now());
        bankSlip.setStatus(BankSlipStatus.PENDING);

        return bankSlip;
    }

    public BankSlipPaymentResponse toResponse(BankSlip bankSlip) {
        return new BankSlipPaymentResponse(
                bankSlip.getBarcode(),
                bankSlip.getAmount(),
                bankSlip.getDueDate(),
                bankSlip.getPayerName(),
                bankSlip.getPayerCpf(),
                bankSlip.getGeneratedAt(),
                bankSlip.getCanceledAt(),
                bankSlip.getStatus()
        );
    }
}
