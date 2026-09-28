package br.edu.utfpr.apiDelivery.delivery.application;

import br.edu.utfpr.apiDelivery.delivery.api.request.DeliveryRequest;
import br.edu.utfpr.apiDelivery.delivery.api.response.DeliveryResponse;
import br.edu.utfpr.apiDelivery.delivery.domain.Delivery;
import br.edu.utfpr.apiDelivery.delivery.infraestructure.mapper.DeliveryMapper;
import br.edu.utfpr.apiDelivery.delivery.infraestructure.persistence.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class CreateDeliveryService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;

    public CreateDeliveryService(
            DeliveryRepository deliveryRepository,
            DeliveryMapper deliveryMapper
    ) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
    }

    @Transactional
    public DeliveryResponse create(DeliveryRequest request) {
        Delivery delivery = deliveryMapper.toDelivery(request);
        delivery.setTrackingCode(generateTrackingCode());

        Delivery savedDelivery = deliveryRepository.save(delivery);
        return deliveryMapper.toResponse(savedDelivery);
    }

    private String generateTrackingCode() {
        String trackingCode;

        do {
            trackingCode = String.format("%010d", RANDOM.nextLong(10_000_000_000L));
        } while (deliveryRepository.existsByTrackingCode(trackingCode));

        return trackingCode;
    }
}