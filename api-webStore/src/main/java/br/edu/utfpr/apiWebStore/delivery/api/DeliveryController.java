package br.edu.utfpr.apiWebStore.delivery.api;

import br.edu.utfpr.apiWebStore.delivery.api.request.DeliveryRequest;
import br.edu.utfpr.apiWebStore.delivery.api.response.DeliveryResponse;
import br.edu.utfpr.apiWebStore.delivery.application.CreateDeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final CreateDeliveryService createDeliveryService;

    public DeliveryController(CreateDeliveryService createDeliveryService) {
        this.createDeliveryService = createDeliveryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeliveryResponse> create(
            @Valid @RequestBody DeliveryRequest request
    ) {
        DeliveryResponse response = createDeliveryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
