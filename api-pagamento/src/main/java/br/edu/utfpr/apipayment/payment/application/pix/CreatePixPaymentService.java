package br.edu.utfpr.apipayment.payment.application.pix;

import br.edu.utfpr.apipayment.payment.api.pix.request.CreatePixPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.pix.response.CreatePixPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.Payment;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.infrastructure.mapper.PixPaymentMapper;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PixRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;

@Service
public class CreatePixPaymentService {

    private static PixRepository pixRepository;
    private static PaymentRepository paymentRepository;
    private static PixPaymentMapper pixPaymentMapper;

    public CreatePixPaymentService(
            PixRepository pixRepository,
            PaymentRepository paymentRepository,
            PixPaymentMapper pixPaymentMapper
    ){
        this.pixRepository = pixRepository;
        this.paymentRepository = paymentRepository;
        this.pixPaymentMapper = pixPaymentMapper;
    }

    @Transactional
    public CreatePixPaymentResponse createPixPayment(CreatePixPaymentRequest pixRequest){

        Payment payment = pixPaymentMapper.toPayment(pixRequest);
        payment.setCreatedAt(LocalDate.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPurchaseId(pixRequest.purchaseId());

        Payment paymentCreated = paymentRepository.save(payment);

        Pix pixPayment = pixPaymentMapper.toPix(pixRequest);
        pixPayment.setPayment(paymentCreated);
        pixPayment.setQrCode(this.gerarCodigoPix());

        Pix pixPaymentCreated = pixRepository.save(pixPayment);

        return new CreatePixPaymentResponse(
                pixPaymentCreated.getId(),
                pixPaymentCreated.getQrCode(),
                pixPaymentCreated.getExpirationDate(),
                pixPaymentCreated.getExpirationTime(),
                pixPaymentCreated.getAmount()
        );

    }

    private String gerarCodigoPix(){
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder result = new StringBuilder(25);

        for (int i = 0; i < 25; i++) {
            result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return result.toString();
    }

}
