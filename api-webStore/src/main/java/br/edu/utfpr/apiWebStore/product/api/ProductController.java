package br.edu.utfpr.apiWebStore.product.api;

import br.edu.utfpr.apiWebStore.product.api.request.CreateProductRequest;
import br.edu.utfpr.apiWebStore.product.api.request.UpdateProductRequest;
import br.edu.utfpr.apiWebStore.product.api.request.StockMovementRequest;
import br.edu.utfpr.apiWebStore.product.api.response.ProductResponse;
import br.edu.utfpr.apiWebStore.product.application.CreateProductService;
import br.edu.utfpr.apiWebStore.product.application.DeleteProductService;
import br.edu.utfpr.apiWebStore.product.application.SearchProductService;
import br.edu.utfpr.apiWebStore.product.application.UpdateProductService;
import br.edu.utfpr.apiWebStore.product.application.StockMovementService;
import br.edu.utfpr.apiWebStore.shared.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CreateProductService createProductService;
    private final UpdateProductService updateProductService;
    private final DeleteProductService deleteProductService;
    private final SearchProductService searchProductService;
    private final StockMovementService stockMovementService;

    public ProductController(
            CreateProductService createProductService,
            UpdateProductService updateProductService,
            DeleteProductService deleteProductService,
            SearchProductService searchProductService,
            StockMovementService stockMovementService
    ) {
        this.createProductService = createProductService;
        this.updateProductService = updateProductService;
        this.deleteProductService = deleteProductService;
        this.searchProductService = searchProductService;
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "A página deve ser maior ou igual a 1") int page,
            @RequestParam(name = "size", defaultValue = "20")
            @Min(value = 1, message = "O tamanho da página deve ser maior ou igual a 1") int size
    ) {
        return ResponseEntity.ok(searchProductService.findAll(page, Math.min(size, MAX_PAGE_SIZE)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(searchProductService.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest request
    ) {
        return createdResponse(createProductService.create(request, null));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createWithImage(
            @Valid @RequestPart("product") CreateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return createdResponse(createProductService.create(request, image));
    }

    private ResponseEntity<ProductResponse> createdResponse(ProductResponse response) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(updateProductService.update(id, request, null));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateWithImage(
            @PathVariable("id") Long id,
            @Valid @RequestPart("product") UpdateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(updateProductService.update(id, request, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        deleteProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stock-movements")
    public ResponseEntity<Void> moveStock(
            @PathVariable("id") Long id,
            @Valid @RequestBody StockMovementRequest request
    ) {
        stockMovementService.moveStock(id, request);
        return ResponseEntity.noContent().build();
    }
}
