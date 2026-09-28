package br.edu.utfpr.apiWebStore.payment.api.request.bankSlip;

import br.edu.utfpr.apiWebStore.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateBankSlipPaymentRequest(
        @NotNull(message = "O id da compra é obrigatário.")
        Long purchaseId,

        @NotNull(message = "O método de pagamento é obrigatário.")
        PaymentMethod paymentMethod,

        @NotBlank(message = "O nome do pagador é obrigatório.")
        @Size(max = 150, message = "O nome do pagador deve conter no máximo 150 caracteres.")
        String payerName,

        @NotBlank(message = "O CPF do pagador é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF do pagador deve conter exatamente 11 dígitos.")
        String payerCpf,

        @NotNull(message = "O valor total é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero.")
        @Digits(integer = 17, fraction = 2, message = "O valor total deve ter no máximo 17 dígitos inteiros e 2 casas decimais.")
        BigDecimal totalAmount
) {
}
