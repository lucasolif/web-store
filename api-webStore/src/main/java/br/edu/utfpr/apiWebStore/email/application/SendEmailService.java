package br.edu.utfpr.apiWebStore.email.application;

import br.edu.utfpr.apiWebStore.email.api.request.EmailSendRequest;
import br.edu.utfpr.apiWebStore.email.domain.enumeration.EmailType;
import br.edu.utfpr.apiWebStore.email.infraestructure.mapper.InvoiceEmailMapper;
import br.edu.utfpr.apiWebStore.email.integration.EmailClient;
import br.edu.utfpr.apiWebStore.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiWebStore.invoice.integration.InvoiceClient;
import br.edu.utfpr.apiWebStore.shared.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {

    private final EmailClient emailClient;
    private final InvoiceClient invoiceClient;
    private final InvoiceEmailMapper invoiceEmailMapper;

    public SendEmailService(
            EmailClient emailClient,
            InvoiceClient invoiceClient,
            InvoiceEmailMapper invoiceEmailMapper
    ) {
        this.emailClient = emailClient;
        this.invoiceClient = invoiceClient;
        this.invoiceEmailMapper = invoiceEmailMapper;
    }

    public void send(EmailSendRequest request) {
        if (request.emailType() != EmailType.INVOICE) {
            emailClient.sendEmail(request);
            return;
        }

        if (request.invoiceId() == null || request.invoiceId() <= 0) {
            throw new ValidationException(
                    "INVALID_INVOICE_ID",
                    "O identificador da nota fiscal é obrigatório para o envio do e-mail."
            );
        }

        InvoiceResponse invoice = invoiceClient.findById(request.invoiceId());
        EmailSendRequest invoiceEmailRequest = new EmailSendRequest(
                request.emailType(),
                request.recipient(),
                request.invoiceId(),
                null,
                invoiceEmailMapper.toRequest(invoice)
        );

        emailClient.sendEmail(invoiceEmailRequest);
    }
}
