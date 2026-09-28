package br.edu.utfpr.apiWebStore.invoice.api;

import br.edu.utfpr.apiWebStore.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiWebStore.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiWebStore.invoice.application.CreateInvoiceService;
import br.edu.utfpr.apiWebStore.invoice.application.CreatePurchaseInvoiceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoice")
@Validated
public class InvoiceController {

    private final CreateInvoiceService createInvoiceService;
    private final CreatePurchaseInvoiceService createPurchaseInvoiceService;

    public InvoiceController(
            CreateInvoiceService createInvoiceService,
            CreatePurchaseInvoiceService createPurchaseInvoiceService
    ) {
        this.createInvoiceService = createInvoiceService;
        this.createPurchaseInvoiceService = createPurchaseInvoiceService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceResponse> create(
            @Valid @RequestBody InvoiceRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createInvoiceService.create(request));
    }

    @PostMapping("/purchase/{purchaseId}")
    public ResponseEntity<InvoiceResponse> createForPurchase(
            @PathVariable @Positive Long purchaseId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createPurchaseInvoiceService.create(purchaseId));
    }
}
