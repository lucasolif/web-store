package br.edu.utfpr.apicep.cep.api.response;

import br.edu.utfpr.apicep.cep.domain.EnderecoCep;

public record EnderecoCepResponse(
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
        String siafi
) {

    public static EnderecoCepResponse from(EnderecoCep endereco) {
        return new EnderecoCepResponse(
                endereco.cep(),
                endereco.logradouro(),
                endereco.complemento(),
                endereco.unidade(),
                endereco.bairro(),
                endereco.localidade(),
                endereco.uf(),
                endereco.estado(),
                endereco.regiao(),
                endereco.ibge(),
                endereco.gia(),
                endereco.ddd(),
                endereco.siafi()
        );
    }
}
