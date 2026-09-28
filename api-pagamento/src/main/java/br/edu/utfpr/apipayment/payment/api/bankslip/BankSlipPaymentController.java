package br.edu.utfpr.apipayment.payment.api.bankslip;

import br.edu.utfpr.apipayment.payment.api.bankslip.request.CreateBankSlipPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.bankslip.response.BankSlipPaymentResponse;
import br.edu.utfpr.apipayment.payment.api.bankslip.response.CreateBankSlipPaymentResponse;
import br.edu.utfpr.apipayment.payment.application.bankslip.ConfirmBankSlipPaymentService;
import br.edu.utfpr.apipayment.payment.application.bankslip.CreateBankSlipPaymentService;
import br.edu.utfpr.apipayment.payment.application.bankslip.CancelBankSlipPaymentService;
import br.edu.utfpr.apipayment.payment.application.bankslip.SearchBankSlipPaymentService;
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
@RequestMapping("/api/payment/bankslip")
public class BankSlipPaymentController {

    private static CreateBankSlipPaymentService createBankSlipPaymentService;
    private static CancelBankSlipPaymentService cancelBankSlipPaymentService;
    private static SearchBankSlipPaymentService searchBankSlipPaymentService;
    private static ConfirmBankSlipPaymentService confirmBankSlipPaymentService;

    public BankSlipPaymentController(
            CreateBankSlipPaymentService createBankSlipPaymentService,
            CancelBankSlipPaymentService cancelBankSlipPaymentService,
            SearchBankSlipPaymentService searchBankSlipPaymentService,
            ConfirmBankSlipPaymentService confirmBankSlipPaymentService
    ){
        this.createBankSlipPaymentService = createBankSlipPaymentService;
        this.cancelBankSlipPaymentService = cancelBankSlipPaymentService;
        this.searchBankSlipPaymentService = searchBankSlipPaymentService;
        this.confirmBankSlipPaymentService = confirmBankSlipPaymentService;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateBankSlipPaymentResponse> create(
            @Valid @RequestBody CreateBankSlipPaymentRequest cardRequest
    ) {
        CreateBankSlipPaymentResponse response = createBankSlipPaymentService.createBankSlipPayment(cardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> confirmPayment(@PathVariable Long id) {
        confirmBankSlipPaymentService.confirmBankSlipPayment(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cancelBankSlipPaymentService.deleteBankSlipPayment(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<BankSlipPaymentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(searchBankSlipPaymentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BankSlipPaymentResponse>> findAll(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "A página deve ser maior ou igual a 1") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "O tamanho da página deve ser maior ou igual a 1") int size
    ) {
        PageRequest pageable = PageRequest.of(page - 1, Math.min(size, 100), Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(searchBankSlipPaymentService.findAll(pageable));
    }
}
