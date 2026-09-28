package br.edu.utfpr.apicep.viaCep.infrastructure;

import br.edu.utfpr.apicep.viaCep.api.response.ViaCepResponse;
import br.edu.utfpr.apicep.viaCep.application.CepProvider;
import br.edu.utfpr.apicep.cep.domain.EnderecoCep;
import br.edu.utfpr.apicep.shared.exception.ViaCepIndisponivelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class ViaCepClient implements CepProvider {

    private final RestClient restClient;

    public ViaCepClient(
            RestClient.Builder restClientBuilder,
            @Value("${integracoes.viacep.base-url}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<EnderecoCep> buscarPorCep(String cep) {
        ViaCepResponse response;

        try {
            response = restClient.get()
                    .uri("/ws/{cep}/json/", cep)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (RestClientException exception) {
            throw new ViaCepIndisponivelException(exception);
        }

        if (response == null) {
            throw new ViaCepIndisponivelException();
        }

        if (Boolean.TRUE.equals(response.erro())) {
            return Optional.empty();
        }

        return Optional.of(response.toDomain());
    }
}
