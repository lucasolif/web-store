package br.edu.utfpr.apiWebStore.payment.integration;

import br.edu.utfpr.apiWebStore.payment.api.request.bankSlip.CreateBankSlipPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.request.card.CreateCardPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.request.pix.CreatePixPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.response.bankSlip.CreateBankSlipPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.api.response.card.CreateCardPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.api.response.pix.CreatePixPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.domain.enumeration.PaymentMethod;
import br.edu.utfpr.apiWebStore.shared.exception.ConflictException;
import br.edu.utfpr.apiWebStore.shared.exception.ExternalServiceException;
import br.edu.utfpr.apiWebStore.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apiWebStore.shared.exception.ValidationException;
import br.edu.utfpr.apiWebStore.shared.exception.web.CorrelationIdFilter;
import br.edu.utfpr.apiWebStore.shared.response.ApiResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
public class PaymentClient {

    private final RestClient restClient;
    private final String pixPaymentPath;
    private final String cardPaymentPath;
    private final String bankSlipPaymentPath;

    public PaymentClient(
            @Value("${integrations.payment.base-url}") String baseUrl,
            @Value("${integrations.payment.connect-timeout:3s}") Duration connectTimeout,
            @Value("${integrations.payment.read-timeout:10s}") Duration readTimeout,
            @Value("${integrations.payment.routes.pix}") String pixPaymentPath,
            @Value("${integrations.payment.routes.card}") String cardPaymentPath,
            @Value("${integrations.payment.routes.bank-slip}") String bankSlipPaymentPath
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

        this.pixPaymentPath = pixPaymentPath;
        this.cardPaymentPath = cardPaymentPath;
        this.bankSlipPaymentPath = bankSlipPaymentPath;
    }

    public CreatePixPaymentResponse createPix(CreatePixPaymentRequest request) {
        try {
            ApiResponse<CreatePixPaymentResponse> response = restClient.post()
                    .uri(pixPaymentPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreatePixPaymentRequest(
                            request.purchaseId(),
                            PaymentMethod.PIX,
                            request.payerName(),
                            request.payerCpf(),
                            request.totalAmount()
                    ))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.data() == null || response.data().id() == null) {
                throw new ExternalServiceException(
                        "INVALID_PAYMENT_API_RESPONSE",
                        "A API de pagamentos não retornou o identificador do PIX."
                );
            }

            return response.data();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_PAYMENT",
                    "Os dados informados para o pagamento são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PAYMENT_CONFLICT",
                    "Não foi possível processar o pagamento devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PAYMENT_API_UNAVAILABLE",
                    "Não foi possível processar o pagamento.",
                    exception
            );
        }
    }

    public CreateCardPaymentResponse createCard(CreateCardPaymentRequest request) {
        try {
            ApiResponse<CreateCardPaymentResponse> response = restClient.post()
                    .uri(cardPaymentPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateCardPaymentRequest(
                            request.purchaseId(),
                            PaymentMethod.CARD,
                            request.buyerName(),
                            request.buyerCpf(),
                            request.totalAmount(),
                            request.installmentCount(),
                            request.cardNumber(),
                            request.securityCode(),
                            request.expirationDate(),
                            request.cardholderName(),
                            request.cardholderCpf()
                    ))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.data() == null || response.data().id() == null) {
                throw new ExternalServiceException(
                        "INVALID_PAYMENT_API_RESPONSE",
                        "A API de pagamentos não retornou o identificador do cartão."
                );
            }

            return response.data();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_PAYMENT",
                    "Os dados informados para o pagamento são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PAYMENT_CONFLICT",
                    "Não foi possível processar o pagamento devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PAYMENT_API_UNAVAILABLE",
                    "Não foi possível processar o pagamento.",
                    exception
            );
        }
    }

    public CreateBankSlipPaymentResponse createBankSlip(CreateBankSlipPaymentRequest request) {
        try {
            ApiResponse<CreateBankSlipPaymentResponse> response = restClient.post()
                    .uri(bankSlipPaymentPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateBankSlipPaymentRequest(
                            request.purchaseId(),
                            PaymentMethod.BANK_SLIP,
                            request.payerName(),
                            request.payerCpf(),
                            request.totalAmount()
                    ))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.data() == null || response.data().id() == null) {
                throw new ExternalServiceException(
                        "INVALID_PAYMENT_API_RESPONSE",
                        "A API de pagamentos não retornou o identificador do boleto."
                );
            }

            return response.data();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_PAYMENT",
                    "Os dados informados para o pagamento são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PAYMENT_CONFLICT",
                    "Não foi possível processar o pagamento devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PAYMENT_API_UNAVAILABLE",
                    "Não foi possível processar o pagamento.",
                    exception
            );
        }
    }

    public void confirmPix(Long id) {
        try {
            restClient.put()
                    .uri(pixPaymentPath + "/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PIX_PAYMENT_NOT_FOUND",
                    "Pagamento PIX não encontrado ou não está pendente."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PAYMENT_CONFLICT",
                    "Não foi possível confirmar o pagamento PIX devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PAYMENT_API_UNAVAILABLE",
                    "Não foi possível confirmar o pagamento PIX.",
                    exception
            );
        }
    }

    public void cancelPix(Long id) {
        try {
            restClient.delete()
                    .uri(pixPaymentPath + "/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PIX_PAYMENT_NOT_FOUND",
                    "Pagamento PIX não encontrado ou não está pendente."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PAYMENT_CONFLICT",
                    "Não foi possível cancelar o pagamento PIX devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PAYMENT_API_UNAVAILABLE",
                    "Não foi possível cancelar o pagamento PIX.",
                    exception
            );
        }
    }

    public void confirmBankSlip(Long id) {
        try {
            restClient.put()
                    .uri(bankSlipPaymentPath + "/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "BANK_SLIP_PAYMENT_NOT_FOUND",
                    "Boleto não encontrado ou não está pendente."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PAYMENT_CONFLICT",
                    "Não foi possível confirmar o boleto devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PAYMENT_API_UNAVAILABLE",
                    "Não foi possível confirmar o boleto.",
                    exception
            );
        }
    }

    public void cancelBankSlip(Long id) {
        try {
            restClient.delete()
                    .uri(bankSlipPaymentPath + "/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "BANK_SLIP_PAYMENT_NOT_FOUND",
                    "Boleto não encontrado ou não está pendente."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PAYMENT_CONFLICT",
                    "Não foi possível cancelar o boleto devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PAYMENT_API_UNAVAILABLE",
                    "Não foi possível cancelar o boleto.",
                    exception
            );
        }
    }

}
