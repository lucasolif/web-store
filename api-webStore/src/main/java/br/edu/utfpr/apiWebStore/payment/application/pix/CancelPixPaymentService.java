package br.edu.utfpr.apiWebStore.payment.application.pix;

import br.edu.utfpr.apiWebStore.payment.integration.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class CancelPixPaymentService {

    private final PaymentClient paymentClient;

    public CancelPixPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void cancel(Long id) {
        paymentClient.cancelPix(id);
    }
}
