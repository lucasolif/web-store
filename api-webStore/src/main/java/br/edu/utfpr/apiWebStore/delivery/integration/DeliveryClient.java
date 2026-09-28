package br.edu.utfpr.apiWebStore.delivery.integration;

import br.edu.utfpr.apiWebStore.delivery.api.request.DeliveryRequest;
import br.edu.utfpr.apiWebStore.delivery.api.response.DeliveryResponse;
import br.edu.utfpr.apiWebStore.shared.exception.ConflictException;
import br.edu.utfpr.apiWebStore.shared.exception.ExternalServiceException;
import br.edu.utfpr.apiWebStore.shared.exception.ValidationException;
import br.edu.utfpr.apiWebStore.shared.exception.web.CorrelationIdFilter;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
public class DeliveryClient {

    private final RestClient restClient;
    private final String createDeliveryPath;

    public DeliveryClient(
            @Value("${integrations.delivery.base-url}") String baseUrl,
            @Value("${integrations.delivery.connect-timeout:3s}") Duration connectTimeout,
            @Value("${integrations.delivery.read-timeout:10s}") Duration readTimeout,
            @Value("${integrations.delivery.routes.create}") String createDeliveryPath
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
                        request.getHeaders().set(
                                CorrelationIdFilter.CORRELATION_ID_HEADER,
                                correlationId
                        );
                    }
                    return execution.execute(request, body);
                })
                .build();

        this.createDeliveryPath = createDeliveryPath;
    }

    public DeliveryResponse create(DeliveryRequest request) {
        try {
            DeliveryResponse response = restClient.post()
                    .uri(createDeliveryPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(DeliveryResponse.class);

            if (response == null
                    || response.id() == null
                    || response.trackingCode() == null
                    || response.trackingCode().isBlank()) {
                throw new ExternalServiceException(
                        "INVALID_DELIVERY_API_RESPONSE",
                        "A API de entrega retornou uma resposta inválida."
                );
            }

            return response;
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_DELIVERY",
                    "Os dados informados para a entrega são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "DELIVERY_CONFLICT",
                    "Não foi possível cadastrar a entrega devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "DELIVERY_API_UNAVAILABLE",
                    "Não foi possível cadastrar a entrega.",
                    exception
            );
        }
    }
}
