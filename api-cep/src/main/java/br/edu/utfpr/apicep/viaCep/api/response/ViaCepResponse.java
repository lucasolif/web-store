package br.edu.utfpr.apicep.viaCep.api.response;

import br.edu.utfpr.apicep.cep.domain.EnderecoCep;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String unidade,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String regiao,
        String ibge,
        String gia,
        String ddd,
        String siafi,
        Boolean erro
) {

    public EnderecoCep toDomain() {
        return new EnderecoCep(
                cep,
                logradouro,
                complemento,
                unidade,
                bairro,
                localidade,
                uf,
                estado,
                regiao,
                ibge,
                gia,
                ddd,
                siafi
        );
    }
}
