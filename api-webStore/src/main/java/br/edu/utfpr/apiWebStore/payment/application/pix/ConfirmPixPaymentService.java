package br.edu.utfpr.apiWebStore.payment.application.pix;

import br.edu.utfpr.apiWebStore.payment.integration.PaymentClient;
import org.springframework.stereotype.Service;

@Service
public class ConfirmPixPaymentService {

    private final PaymentClient paymentClient;

    public ConfirmPixPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void confirm(Long id) {
        paymentClient.confirmPix(id);
    }
}
