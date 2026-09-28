package br.edu.utfpr.apiWebStore.email.integration;

import br.edu.utfpr.apiWebStore.email.api.request.EmailSendRequest;
import br.edu.utfpr.apiWebStore.email.domain.enumeration.EmailType;
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
public class EmailClient {

    private final RestClient restClient;
    private final String sendEmailPath;

    public EmailClient(
            @Value("${integrations.email.base-url}") String baseUrl,
            @Value("${integrations.email.connect-timeout:3s}") Duration connectTimeout,
            @Value("${integrations.email.read-timeout:10s}") Duration readTimeout,
            @Value("${integrations.email.routes.send}") String sendEmailPath
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

        this.sendEmailPath = sendEmailPath;
    }

    public void sendEmail(EmailSendRequest emailSendRequest) {
        try {
            restClient.post()
                    .uri(sendEmailPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(emailSendRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_EMAIL_REQUEST",
                    "Os dados informados para o envio do e-mail são inválidos."
            );
        } catch (RestClientException exception) {
            String message = switch (emailSendRequest.emailType()) {
                case PAYMENT -> "Não foi possível enviar o e-mail de confirmação do pagamento.";
                case INVOICE -> "Não foi possível enviar o e-mail com a nota fiscal.";
                case DELIVERY -> "Não foi possível enviar o e-mail com os dados da entrega.";
                case BUY -> "Não foi possível enviar o e-mail de confirmação da compra.";
            };
            throw new ExternalServiceException(
                    "EMAIL_API_UNAVAILABLE",
                    message,
                    exception
            );
        }
    }

}
