package br.edu.utfpr.apiWebStore.buy.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateBuyerRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "\\d{11}") String cpf,
        @NotBlank @Email String email,
        @NotBlank String cellphone
) {
}
