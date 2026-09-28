package br.edu.utfpr.apiWebStore.payment.api;

import br.edu.utfpr.apiWebStore.payment.api.request.bankSlip.CreateBankSlipPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.request.card.CreateCardPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.request.pix.CreatePixPaymentRequest;
import br.edu.utfpr.apiWebStore.payment.api.response.bankSlip.CreateBankSlipPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.api.response.card.CreateCardPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.api.response.pix.CreatePixPaymentResponse;
import br.edu.utfpr.apiWebStore.payment.application.bankSlip.CancelBankSlipPaymentService;
import br.edu.utfpr.apiWebStore.payment.application.bankSlip.ConfirmBankSlipPaymentService;
import br.edu.utfpr.apiWebStore.payment.application.bankSlip.CreateBankSlipPaymentService;
import br.edu.utfpr.apiWebStore.payment.application.card.CreateCardPaymentService;
import br.edu.utfpr.apiWebStore.payment.application.pix.CancelPixPaymentService;
import br.edu.utfpr.apiWebStore.payment.application.pix.ConfirmPixPaymentService;
import br.edu.utfpr.apiWebStore.payment.application.pix.CreatePixPaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@Validated
public class PaymentController {

    private final CreatePixPaymentService createPixPaymentService;
    private final CreateCardPaymentService createCardPaymentService;
    private final CreateBankSlipPaymentService createBankSlipPaymentService;
    private final ConfirmPixPaymentService confirmPixPaymentService;
    private final CancelPixPaymentService cancelPixPaymentService;
    private final ConfirmBankSlipPaymentService confirmBankSlipPaymentService;
    private final CancelBankSlipPaymentService cancelBankSlipPaymentService;

    public PaymentController(
            CreatePixPaymentService createPixPaymentService,
            CreateCardPaymentService createCardPaymentService,
            CreateBankSlipPaymentService createBankSlipPaymentService,
            ConfirmPixPaymentService confirmPixPaymentService,
            CancelPixPaymentService cancelPixPaymentService,
            ConfirmBankSlipPaymentService confirmBankSlipPaymentService,
            CancelBankSlipPaymentService cancelBankSlipPaymentService
    ) {
        this.createPixPaymentService = createPixPaymentService;
        this.createCardPaymentService = createCardPaymentService;
        this.createBankSlipPaymentService = createBankSlipPaymentService;
        this.confirmPixPaymentService = confirmPixPaymentService;
        this.cancelPixPaymentService = cancelPixPaymentService;
        this.confirmBankSlipPaymentService = confirmBankSlipPaymentService;
        this.cancelBankSlipPaymentService = cancelBankSlipPaymentService;
    }

    @PostMapping(value = "/pix", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePixPaymentResponse> createPix(
            @Valid @RequestBody CreatePixPaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createPixPaymentService.create(request));
    }

    @PostMapping(value = "/card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateCardPaymentResponse> createCard(
            @Valid @RequestBody CreateCardPaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createCardPaymentService.create(request));
    }

    @PostMapping(value = "/bankslip", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateBankSlipPaymentResponse> createBankSlip(
            @Valid @RequestBody CreateBankSlipPaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBankSlipPaymentService.create(request));
    }

    @PutMapping("/pix/{id}")
    public ResponseEntity<Void> confirmPix(@PathVariable @Positive Long id) {
        confirmPixPaymentService.confirm(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/pix/{id}")
    public ResponseEntity<Void> cancelPix(@PathVariable @Positive Long id) {
        cancelPixPaymentService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/bankslip/{id}")
    public ResponseEntity<Void> confirmBankSlip(@PathVariable @Positive Long id) {
        confirmBankSlipPaymentService.confirm(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bankslip/{id}")
    public ResponseEntity<Void> cancelBankSlip(@PathVariable @Positive Long id) {
        cancelBankSlipPaymentService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
