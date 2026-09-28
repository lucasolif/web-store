package br.edu.utfpr.apipayment.payment.api.bankslip.request;

import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateBankSlipPaymentRequest(

        @NotNull(message = "O id da compra é obrigatário.")
        Long purchaseId,

        @NotNull(message = "O método de pagamento é obrigatório.")
        PaymentMethod paymentMethod,

        @NotBlank(message = "O nome do pagador é obrigatório.")
        @Size(max = 150, message = "O nome do comprador deve conter no máximo 150 caracteres.")
        String payerName,

        @NotBlank(message = "O CPF do pagador é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF do comprador deve conter exatamente 11 dígitos.")
        String payerCpf,

        @NotNull(message = "O valor total é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero.")
        @Digits(integer = 17, fraction = 2, message = "O valor total deve ter no máximo 17 dígitos inteiros e 2 casas decimais.")
        BigDecimal totalAmount
) {
}
