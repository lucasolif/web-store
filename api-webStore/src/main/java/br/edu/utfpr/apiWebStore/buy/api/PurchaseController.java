package br.edu.utfpr.apiWebStore.buy.api;

import br.edu.utfpr.apiWebStore.buy.api.request.CreatePurchaseRequest;
import br.edu.utfpr.apiWebStore.buy.api.response.PurchaseResponse;
import br.edu.utfpr.apiWebStore.buy.application.CancelPurchaseService;
import br.edu.utfpr.apiWebStore.buy.application.ConfirmPurchaseService;
import br.edu.utfpr.apiWebStore.buy.application.CreatePurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final CreatePurchaseService createPurchaseService;
    private final ConfirmPurchaseService confirmPurchaseService;
    private final CancelPurchaseService cancelPurchaseService;

    public PurchaseController(
            CreatePurchaseService createPurchaseService,
            ConfirmPurchaseService confirmPurchaseService,
            CancelPurchaseService cancelPurchaseService
    ){
        this.createPurchaseService = createPurchaseService;
        this.confirmPurchaseService = confirmPurchaseService;
        this.cancelPurchaseService = cancelPurchaseService;
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> create(
            @Valid @RequestBody CreatePurchaseRequest request
    ) {
        PurchaseResponse response = createPurchaseService.createPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id) {
        confirmPurchaseService.confirmPurchase(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        cancelPurchaseService.canccelPurchase(id);
        return ResponseEntity.noContent().build();
    }
}
