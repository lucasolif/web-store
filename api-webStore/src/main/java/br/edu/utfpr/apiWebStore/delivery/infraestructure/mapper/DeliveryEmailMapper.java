package br.edu.utfpr.apiWebStore.delivery.infraestructure.mapper;

import br.edu.utfpr.apiWebStore.delivery.api.response.DeliveryResponse;
import br.edu.utfpr.apiWebStore.email.api.request.DeliveryRequest;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEmailMapper {

    public DeliveryRequest toRequest(DeliveryResponse response) {
        return new DeliveryRequest(
                response.id(),
                response.trackingCode(),
                response.deliveryPrevision(),
                response.customer(),
                response.customerCpf(),
                response.customerEmail(),
                response.customerAddressStreet(),
                response.customerAddressNumber(),
                response.customerAddressNeighborhood(),
                response.customerAddressCep(),
                response.customerAddressCity(),
                response.customerAddressState(),
                response.customerAddressComplement()
        );
    }
}
