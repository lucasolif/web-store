package br.edu.utfpr.apiWebStore.payment.application.bankSlip;

import br.edu.utfpr.apiWebStore.payment.integration.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class ConfirmBankSlipPaymentService {

    private final PaymentClient paymentClient;

    public ConfirmBankSlipPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void confirm(Long id) {
        paymentClient.confirmBankSlip(id);
    }
}
