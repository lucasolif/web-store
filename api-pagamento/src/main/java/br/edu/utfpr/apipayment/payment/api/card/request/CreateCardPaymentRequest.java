package br.edu.utfpr.apipayment.payment.api.card.request;

import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCardPaymentRequest(

        @NotNull(message = "O id da compra é obrigatário.")
        Long purchaseId,

        @NotNull(message = "O método de pagamento é obrigatório.")
        PaymentMethod paymentMethod,

        @NotBlank(message = "O nome do comprador é obrigatório.")
        @Size(max = 150, message = "O nome do comprador deve conter no máximo 150 caracteres.")
        String buyerName,

        @NotBlank(message = "O CPF do comprador é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF do comprador deve conter exatamente 11 dígitos.")
        String buyerCpf,

        @NotNull(message = "O valor total é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero.")
        @Digits(integer = 17, fraction = 2, message = "O valor total deve ter no máximo 17 dígitos inteiros e 2 casas decimais.")
        BigDecimal totalAmount,

        @Min(value = 1, message = "A quantidade de parcelas deve ser no mínimo 1.")
        @Max(value = 12, message = "A quantidade de parcelas deve ser no máximo 12.")
        Integer installmentCount,

        @NotBlank(message = "O número do cartão é obrigatório.")
        @Pattern(regexp = "\\d{13,19}", message = "O número do cartão deve conter entre 13 e 19 dígitos.")
        String cardNumber,

        @NotBlank(message = "O código de segurança é obrigatório.")
        @Pattern(regexp = "\\d{3,4}", message = "O código de segurança deve conter 3 ou 4 dígitos.")
        String securityCode,

        @NotBlank(message = "A data de validade do cartão é obrigatória.")
        @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "A data de validade do cartão deve utilizar o formato MM/aa.")
        String expirationDate,

        @NotBlank(message = "O nome do titular do cartão é obrigatório.")
        @Size(max = 150, message = "O nome do titular do cartão deve conter no máximo 150 caracteres.")
        String cardholderName,

        @NotBlank(message = "O CPF do titular do cartão é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF do titular do cartão deve conter exatamente 11 dígitos.")
        String cardholderCpf
) {
}
