package br.edu.utfpr.apiEmail.email.api.request;

import br.edu.utfpr.apiEmail.email.domain.EmailType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailSendRequest(
        @NotNull(message = "O tipo do e-mail é obrigatório.")
        EmailType emailType,

        @NotBlank(message = "O destinatário é obrigatório.")
        String recipient,

        Long invoiceId,

        DeliveryRequest delivery,

        InvoiceRequest invoice
) {
}
