package br.edu.utfpr.apiWebStore.invoice.integration;

import br.edu.utfpr.apiWebStore.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiWebStore.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiWebStore.shared.exception.ConflictException;
import br.edu.utfpr.apiWebStore.shared.exception.ExternalServiceException;
import br.edu.utfpr.apiWebStore.shared.exception.ResourceNotFoundException;
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
public class InvoiceClient {

    private final RestClient restClient;
    private final String invoicesPath;
    private final String invoiceByIdPath;

    public InvoiceClient(
            @Value("${integrations.nf.base-url}") String baseUrl,
            @Value("${integrations.nf.connect-timeout:3s}") Duration connectTimeout,
            @Value("${integrations.nf.read-timeout:10s}") Duration readTimeout,
            @Value("${integrations.nf.routes.create}") String invoicesPath,
            @Value("${integrations.nf.routes.by-id}") String invoiceByIdPath
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

        this.invoicesPath = invoicesPath;
        this.invoiceByIdPath = invoiceByIdPath;
    }

    public InvoiceResponse create(InvoiceRequest request) {
        try {
            InvoiceResponse response = restClient.post()
                    .uri(invoicesPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(InvoiceResponse.class);

            if (response == null || response.id() == null) {
                throw new ExternalServiceException(
                        "INVALID_INVOICE_API_RESPONSE",
                        "A API de nota fiscal retornou uma resposta inválida."
                );
            }

            return response;
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_INVOICE",
                    "Os dados informados para a nota fiscal são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "INVOICE_CONFLICT",
                    "Não foi possível cadastrar a nota fiscal devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "INVOICE_API_UNAVAILABLE",
                    "Não foi possível cadastrar a nota fiscal.",
                    exception
            );
        }
    }

    public InvoiceResponse findById(Long invoiceId) {
        try {
            InvoiceResponse response = restClient.get()
                    .uri(invoiceByIdPath, invoiceId)
                    .retrieve()
                    .body(InvoiceResponse.class);

            if (response == null || response.id() == null) {
                throw new ExternalServiceException(
                        "INVALID_INVOICE_API_RESPONSE",
                        "A API de nota fiscal retornou uma resposta inválida."
                );
            }

            return response;
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "INVOICE_NOT_FOUND",
                    "A nota fiscal não foi encontrada."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "INVOICE_API_UNAVAILABLE",
                    "Não foi possível consultar a nota fiscal.",
                    exception
            );
        }
    }
}
