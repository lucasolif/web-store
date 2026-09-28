package br.edu.utfpr.apipayment.payment.api.card;

import br.edu.utfpr.apipayment.payment.api.card.request.CreateCardPaymentRequest;
import br.edu.utfpr.apipayment.payment.api.card.response.CardPaymentResponse;
import br.edu.utfpr.apipayment.payment.api.card.response.CreatedCardPaymentResponse;
import br.edu.utfpr.apipayment.payment.application.card.CreateCardPaymentService;
import br.edu.utfpr.apipayment.payment.application.card.DeleteCardPaymentService;
import br.edu.utfpr.apipayment.payment.application.card.SearchCardPaymentService;
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
@RequestMapping("/api/payment/card")
public class CardPaymentController {

    private static CreateCardPaymentService createCardPaymentService;
    private static DeleteCardPaymentService deleteCardPaymentService;
    private static SearchCardPaymentService searchCardPaymentService;

    public CardPaymentController(
            CreateCardPaymentService createCardPaymentService,
            DeleteCardPaymentService deleteCardPaymentService,
            SearchCardPaymentService searchCardPaymentService
    ){
        this.createCardPaymentService = createCardPaymentService;
        this.deleteCardPaymentService = deleteCardPaymentService;
        this.searchCardPaymentService = searchCardPaymentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedCardPaymentResponse> create(
            @Valid @RequestBody CreateCardPaymentRequest cardRequest
    ) {
        CreatedCardPaymentResponse response = createCardPaymentService.createdCardPayment(cardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteCardPaymentService.deleteCardPayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardPaymentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(searchCardPaymentService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CardPaymentResponse>> findAll(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "A página deve ser maior ou igual a 1") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "O tamanho da página deve ser maior ou igual a 1") int size
    ) {
        PageRequest pageable = PageRequest.of(page - 1, Math.min(size, 100), Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(searchCardPaymentService.findAll(pageable));
    }



}
