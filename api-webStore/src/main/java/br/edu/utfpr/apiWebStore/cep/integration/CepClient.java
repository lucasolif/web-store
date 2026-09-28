package br.edu.utfpr.apiWebStore.cep.integration;

import br.edu.utfpr.apiWebStore.cep.api.response.AddressResponse;
import br.edu.utfpr.apiWebStore.shared.exception.UpstreamApiException;
import br.edu.utfpr.apiWebStore.shared.exception.web.CorrelationIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
public class CepClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String cepPath;

    public CepClient(
            ObjectMapper objectMapper,
            @Value("${integrations.cep.base-url}") String baseUrl,
            @Value("${integrations.cep.connect-timeout:10s}") Duration connectTimeout,
            @Value("${integrations.cep.read-timeout:10s}") Duration readTimeout,
            @Value("${integrations.cep.routes.cep}") String cepPath
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    String correlationId = MDC.get("correlationId");
                    if (correlationId != null && !correlationId.isBlank()) {
                        request.getHeaders().set(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
                    }
                    return execution.execute(request, body);
                })
                .build();
        this.objectMapper = objectMapper;
        this.cepPath = cepPath;
    }

    public AddressResponse findAddressByCep(String cep) {
        try {
            AddressResponse response = restClient.get()
                    .uri(cepPath, cep)
                    .retrieve()
                    .body(AddressResponse.class);

            if (response == null || response.cep() == null) {
                throw new UpstreamApiException(
                        HttpStatus.BAD_GATEWAY,
                        "INVALID_CEP_API_RESPONSE",
                        "A API de CEP retornou uma resposta inválida.",
                        List.of(),
                        null,
                        null
                );
            }
            return response;
        } catch (RestClientResponseException exception) {
            throw new UpstreamApiException(
                    exception.getStatusCode(),
                    "CEP_API_ERROR",
                    extractMessage(exception),
                    List.of(),
                    null,
                    exception
            );
        } catch (ResourceAccessException exception) {
            throw new UpstreamApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "CEP_API_UNAVAILABLE",
                    "O serviço de consulta de CEP está indisponível no momento.",
                    List.of(),
                    null,
                    exception
            );
        }
    }

    private String extractMessage(RestClientResponseException exception) {
        byte[] body = exception.getResponseBodyAsByteArray();
        if (body.length > 0) {
            try {
                String message = objectMapper.readTree(body).path("message").asText();
                if (!message.isBlank()) return message;
            } catch (IOException ignored) {
            }
        }
        return "A API de CEP retornou o status " + exception.getStatusCode().value() + ".";
    }
}
