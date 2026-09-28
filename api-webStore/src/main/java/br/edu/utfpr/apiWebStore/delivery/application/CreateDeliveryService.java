package br.edu.utfpr.apiWebStore.delivery.application;

import br.edu.utfpr.apiWebStore.delivery.api.request.DeliveryRequest;
import br.edu.utfpr.apiWebStore.delivery.api.response.DeliveryResponse;
import br.edu.utfpr.apiWebStore.delivery.infraestructure.mapper.DeliveryEmailMapper;
import br.edu.utfpr.apiWebStore.delivery.integration.DeliveryClient;
import br.edu.utfpr.apiWebStore.email.api.request.EmailSendRequest;
import br.edu.utfpr.apiWebStore.email.application.SendEmailService;
import br.edu.utfpr.apiWebStore.email.domain.enumeration.EmailType;
import org.springframework.stereotype.Service;

@Service
public class CreateDeliveryService {

    private final DeliveryClient deliveryClient;
    private final DeliveryEmailMapper deliveryEmailMapper;
    private final SendEmailService sendEmailService;

    public CreateDeliveryService(
            DeliveryClient deliveryClient,
            DeliveryEmailMapper deliveryEmailMapper,
            SendEmailService sendEmailService
    ) {
        this.deliveryClient = deliveryClient;
        this.deliveryEmailMapper = deliveryEmailMapper;
        this.sendEmailService = sendEmailService;
    }

    public DeliveryResponse create(DeliveryRequest request) {
        DeliveryResponse response = deliveryClient.create(request);

        sendEmailService.send(new EmailSendRequest(
                EmailType.DELIVERY,
                response.customerEmail(),
                null,
                deliveryEmailMapper.toRequest(response),
                null
        ));

        return response;
    }
}
