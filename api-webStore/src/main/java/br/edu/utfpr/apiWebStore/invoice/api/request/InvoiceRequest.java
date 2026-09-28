package br.edu.utfpr.apiWebStore.invoice.api.request;

import br.edu.utfpr.apiWebStore.invoice.domain.enumeration.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(
        @NotBlank(message = "O nome do cliente é obrigatório.")
        String customer,

        @NotBlank(message = "O CPF do cliente é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos.")
        String customerCpf,

        @NotBlank(message = "O e-mail do cliente é obrigatório.")
        @Email(message = "O e-mail do cliente é inválido.")
        String customerEmail,

        @NotBlank(message = "O celular do cliente é obrigatório.")
        String customerCelphone,

        @NotBlank(message = "A rua é obrigatória.")
        String customerAddressStreet,

        @NotBlank(message = "O número é obrigatório.")
        String customerAddressNumber,

        @NotBlank(message = "O bairro é obrigatório.")
        String customerAddressNeighborhood,

        @NotBlank(message = "O CEP é obrigatório.")
        String customerAddressCep,

        @NotBlank(message = "A cidade é obrigatória.")
        String customerAddressCity,

        @NotBlank(message = "O estado é obrigatório.")
        String customerAddressState,

        String customerAddressComplement,

        @NotNull(message = "O identificador da compra é obrigatório.")
        @Positive(message = "O identificador da compra deve ser maior que zero.")
        Long purchaseId,

        LocalDate createdAt,
        LocalDate canceledAt,
        InvoiceStatus status,

        @NotEmpty(message = "A nota fiscal deve possuir pelo menos um produto.")
        List<@Valid InvoiceProductRequest> products
) {}