package br.edu.utfpr.apiNotaFiscal.invoice.api;


import br.edu.utfpr.apiNotaFiscal.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiNotaFiscal.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiNotaFiscal.invoice.application.CreateInvoiceService;
import br.edu.utfpr.apiNotaFiscal.invoice.application.SearchInvoiceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@Validated
public class InvoiceController {

    private final CreateInvoiceService createInvoiceService;
    private final SearchInvoiceService searchInvoiceService;

    public InvoiceController(
            CreateInvoiceService createInvoiceService,
            SearchInvoiceService searchInvoiceService
    ) {
        this.createInvoiceService = createInvoiceService;
        this.searchInvoiceService = searchInvoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(
            @Valid @RequestBody InvoiceRequest request
    ) {
        InvoiceResponse response = createInvoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> findById(
            @PathVariable("invoiceId") @Positive Long invoiceId
    ) {
        return ResponseEntity.ok(searchInvoiceService.findById(invoiceId));
    }

}
