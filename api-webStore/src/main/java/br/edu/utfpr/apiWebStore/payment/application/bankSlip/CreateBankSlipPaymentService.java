package br.edu.utfpr.apiWebStore.payment.application.bankSlip;

import br.edu.utfpr.apiWebStore.payment.api.request.bankSlip.CreateBankSlipPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.response.bankSlip.CreateBankSlipPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.integration.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class CreateBankSlipPaymentService {

    private final PaymentClient paymentClient;

    public CreateBankSlipPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public CreateBankSlipPaymentResponse create(CreateBankSlipPaymentRequest request) {
        return paymentClient.createBankSlip(request);
    }
}
