package br.edu.utfpr.apiWebStore.payment.application.card;

import br.edu.utfpr.apiWebStore.payment.api.request.card.CreateCardPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.response.card.CreateCardPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.integration.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class CreateCardPaymentService {

    private final PaymentClient paymentClient;

    public CreateCardPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public CreateCardPaymentResponse create(CreateCardPaymentRequest request) {
        return paymentClient.createCard(request);
    }
}
