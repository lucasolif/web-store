package br.edu.utfpr.apiWebStore.email.api.request;

import br.edu.utfpr.apiWebStore.email.domain.enumeration.EmailType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailSendRequest(
        @NotNull(message = "O tipo do e-mail é obrigatório.")
        EmailType emailType,

        @NotBlank(message = "O destinatário é obrigatório.")
        @Email(message = "O e-mail do destinatário deve ser válido.")
        String recipient,

        Long invoiceId,
        
        DeliveryRequest delivery,

        InvoiceRequest invoice
) { }
