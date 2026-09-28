package br.edu.utfpr.apiWebStore.payment.application.bankSlip;

import br.edu.utfpr.apiWebStore.payment.integration.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class CancelBankSlipPaymentService {

    private final PaymentClient paymentClient;

    public CancelBankSlipPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void cancel(Long id) {
        paymentClient.cancelBankSlip(id);
    }
}
