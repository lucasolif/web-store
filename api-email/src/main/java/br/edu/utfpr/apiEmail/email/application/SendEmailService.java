package br.edu.utfpr.apiEmail.email.application;

import br.edu.utfpr.apiEmail.email.api.request.DeliveryRequest;
import br.edu.utfpr.apiEmail.email.api.request.EmailSendRequest;
import br.edu.utfpr.apiEmail.email.api.request.InvoiceProductRequest;
import br.edu.utfpr.apiEmail.email.api.request.InvoiceRequest;
import br.edu.utfpr.apiEmail.email.domain.EmailMessage;
import br.edu.utfpr.apiEmail.email.domain.EmailType;
import br.edu.utfpr.apiEmail.email.infraestructure.EmailMessageRepository;
import br.edu.utfpr.apiEmail.shared.exception.EmailSendingException;
import br.edu.utfpr.apiEmail.shared.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SendEmailService {

    private final JavaMailSender mailSender;
    private final String senderAddress;
    private final EmailMessageRepository emailMessageRepository;

    public SendEmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String senderAddress,
            EmailMessageRepository emailMessageRepository
    ) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
        this.emailMessageRepository = emailMessageRepository;
    }

    @Transactional
    public void sendingEmail(EmailSendRequest emailRequest) {
        validateRequest(emailRequest);

        if (emailRequest.emailType() == EmailType.INVOICE) {
            validateInvoice(emailRequest.invoice());
        }

        EmailMessage emailMessage = createEmailMessage(emailRequest);
        this.sending(emailMessage);
        this.savedMessage(emailMessage);
    }

    private EmailMessage createEmailMessage(EmailSendRequest emailRequest) {

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipient(emailRequest.recipient());
        emailMessage.setEmailType(emailRequest.emailType());

        this.createMessage(emailMessage, emailRequest.delivery(), emailRequest.invoice());
        return emailMessage;
    }

    private void validateRequest(EmailSendRequest emailRequest) {
        if (emailRequest == null || emailRequest.emailType() == null) {
            throw new ValidationException(
                    "INVALID_EMAIL_TYPE",
                    "O tipo do e-mail é obrigatório."
            );
        }

        if (emailRequest.recipient() == null || emailRequest.recipient().isBlank()) {
            throw new ValidationException(
                    "INVALID_EMAIL_RECIPIENT",
                    "O destinatário é obrigatório."
            );
        }
    }

    private void validateInvoice(InvoiceRequest invoice) {
        if (invoice == null || invoice.id() == null) {
            throw new ValidationException(
                    "INVALID_INVOICE",
                    "Os dados da nota fiscal são obrigatórios."
            );
        }

        if (invoice.products() == null || invoice.products().isEmpty()) {
            throw new ValidationException(
                    "INVOICE_WITHOUT_PRODUCTS",
                    "A nota fiscal deve possuir produtos."
            );
        }
    }

    private void createMessage(EmailMessage emailMessage, DeliveryRequest delivery, InvoiceRequest invoice) {

        String text = "";
        String subject = "";

        if (emailMessage.getEmailType() == EmailType.BUY) {
            subject = "Recebemos seu pedido";
            text = "Seu pedido foi recebido com sucesso. Você receberá novas informações por e-mail conforme o pagamento e a entrega avançarem.";

        } else if (emailMessage.getEmailType() == EmailType.DELIVERY) {
            subject = "Seu pedido saiu para entrega";

            String address = delivery.customerAddressStreet()
                    + ", " + delivery.customerAddressNumber()
                    + " - " + delivery.customerAddressNeighborhood()
                    + ", " + delivery.customerAddressCity()
                    + "/" + delivery.customerAddressState()
                    + " - CEP " + delivery.customerAddressCep();

            if (delivery.customerAddressComplement() != null && !delivery.customerAddressComplement().isBlank()) {
                address += "\nComplemento: " + delivery.customerAddressComplement();
            }

            text = """
        Olá, %s!

        Seu pedido saiu para entrega.

        Código de rastreio: %s
        Previsão de entrega: %s

        Endereço de entrega:
        %s

        Você receberá novas informações quando houver atualização na entrega.
        """.formatted(
                    delivery.customer(),
                    delivery.trackingCode(),
                    delivery.deliveryPrevision().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ),
                    address
            );

        } else if (emailMessage.getEmailType() == EmailType.PAYMENT) {
            subject = "Pagamento confirmado";
            text = "Confirmamos o pagamento do seu pedido. Em breve, você receberá novas informações sobre a entrega.";

        } else if (emailMessage.getEmailType() == EmailType.INVOICE) {
            subject = "Nota fiscal emitida";
            text = createInvoiceMessage(invoice);
        }

        emailMessage.setBody(text);
        emailMessage.setSubject(subject);
    }

    private String createInvoiceMessage(InvoiceRequest invoice) {
        StringBuilder products = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceProductRequest product : invoice.products()) {
            products.append("Produto #")
                    .append(product.productId())
                    .append("\nQuantidade: ")
                    .append(formatQuantity(product.productQuantity()))
                    .append("\nValor unitário: ")
                    .append(formatCurrency(product.productPrice()))
                    .append("\nSubtotal: ")
                    .append(formatCurrency(product.totalAmount()))
                    .append("\n\n");

            total = total.add(product.totalAmount());
        }

        String complement = invoice.customerAddressComplement() == null
                || invoice.customerAddressComplement().isBlank()
                ? "Não informado"
                : invoice.customerAddressComplement();

        return """
                Olá, %s!

                A nota fiscal do seu pedido foi emitida com sucesso.

                NOTA FISCAL
                Número: %s
                Pedido: %s
                Data de emissão: %s
                Status: %s

                CLIENTE
                Nome: %s
                CPF: %s
                E-mail: %s
                Celular: %s

                ENDEREÇO
                %s, %s
                Bairro: %s
                Cidade/Estado: %s/%s
                CEP: %s
                Complemento: %s

                PRODUTOS
                %sTOTAL DA NOTA: %s
                """.formatted(
                invoice.customer(),
                invoice.id(),
                invoice.purchaseId(),
                invoice.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                invoice.status(),
                invoice.customer(),
                invoice.customerCpf(),
                invoice.customerEmail(),
                invoice.customerCelphone(),
                invoice.customerAddressStreet(),
                invoice.customerAddressNumber(),
                invoice.customerAddressNeighborhood(),
                invoice.customerAddressCity(),
                invoice.customerAddressState(),
                invoice.customerAddressCep(),
                complement,
                products,
                formatCurrency(total)
        );
    }

    private String formatCurrency(BigDecimal value) {
        return "R$ " + value
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString()
                .replace('.', ',');
    }

    private String formatQuantity(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    private void sending(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(emailMessage.getRecipient());
        message.setSubject(emailMessage.getSubject());
        message.setText(emailMessage.getBody());

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            throw new EmailSendingException(
                    "Não foi possível enviar o e-mail no momento. Tente novamente mais tarde.",
                    exception
            );
        }
    }

    private void savedMessage(EmailMessage emailMessage){
        emailMessage.setSentAt(LocalDateTime.now());
        emailMessage.setSender(senderAddress);
        emailMessageRepository.save(emailMessage);
    }
}
