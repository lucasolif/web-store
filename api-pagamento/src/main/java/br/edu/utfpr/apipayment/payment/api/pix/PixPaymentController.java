package br.edu.utfpr.apipayment.payment.api.pix;

import br.edu.utfpr.apipayment.payment.api.pix.request.CreatePixPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.pix.response.CreatePixPaymentResponse;
import br.edu.utfpr.apipayment.payment.api.pix.response.PixPaymentResponse;
import br.edu.utfpr.apipayment.payment.application.pix.ConfirmPixPaymentService;
import br.edu.utfpr.apipayment.payment.application.pix.CreatePixPaymentService;
import br.edu.utfpr.apipayment.payment.application.pix.CancelPixPaymentService;
import br.edu.utfpr.apipayment.payment.application.pix.SearchPixPaymentService;
import br.edu.utfpr.apipayment.shared.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/pix")
public class PixPaymentController {

    private static CreatePixPaymentService createPixPaymentService;
    private static CancelPixPaymentService cancelPixPaymentService;
    private static SearchPixPaymentService searchPixPaymentService;
    private static ConfirmPixPaymentService confirmPixPaymentService;

    public PixPaymentController(
            CreatePixPaymentService createPixPaymentService,
            CancelPixPaymentService cancelPixPaymentService,
            SearchPixPaymentService searchPixPaymentService,
            ConfirmPixPaymentService confirmPixPaymentService
    ){
        this.createPixPaymentService = createPixPaymentService;
        this.cancelPixPaymentService = cancelPixPaymentService;
        this.searchPixPaymentService = searchPixPaymentService;
        this.confirmPixPaymentService = confirmPixPaymentService;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePixPaymentResponse> create(
            @Valid @RequestBody CreatePixPaymentRequest cardRequest
    ) {
        CreatePixPaymentResponse response = createPixPaymentService.createPixPayment(cardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cancelPixPaymentService.deletePixPayment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> confirmPayment(@PathVariable Long id) {
        confirmPixPaymentService.confirmPixPayment(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<PixPaymentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(searchPixPaymentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PixPaymentResponse>> findAll(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "A página deve ser maior ou igual a 1") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "O tamanho da página deve ser maior ou igual a 1") int size
    ) {
        PageRequest pageable = PageRequest.of(page - 1, Math.min(size, 100), Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(searchPixPaymentService.findAll(pageable));
    }

}
