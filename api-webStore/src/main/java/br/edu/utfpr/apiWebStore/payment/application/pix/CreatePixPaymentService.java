package br.edu.utfpr.apiWebStore.payment.application.pix;

import br.edu.utfpr.apiWebStore.payment.api.request.pix.CreatePixPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.response.pix.CreatePixPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.integration.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class CreatePixPaymentService {

    private final PaymentClient paymentClient;

    public CreatePixPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public CreatePixPaymentResponse create(CreatePixPaymentRequest request) {
        return paymentClient.createPix(request);
    }
}
