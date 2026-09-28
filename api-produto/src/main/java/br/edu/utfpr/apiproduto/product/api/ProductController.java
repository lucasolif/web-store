package br.edu.utfpr.apiproduto.product.api;

import br.edu.utfpr.apiproduto.product.api.request.CreateProductRequest;
import br.edu.utfpr.apiproduto.product.api.request.StockMovementRequest;
import br.edu.utfpr.apiproduto.product.api.request.UpdateProductRequest;
import br.edu.utfpr.apiproduto.product.api.response.ProductResponse;
import br.edu.utfpr.apiproduto.product.application.*;
import br.edu.utfpr.apiproduto.shared.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CreateProductService createProductService;
    private final FindProductService findProductService;
    private final ListProductsService listProductsService;
    private final UpdateProductService updateProductService;
    private final DeleteProductService deleteProductService;
    private final StockMovementService stockMovementService;

    public ProductController(
            CreateProductService createProductService,
            FindProductService findProductService,
            ListProductsService listProductsService,
            UpdateProductService updateProductService,
            DeleteProductService deleteProductService,
            StockMovementService stockMovementService
    ) {
        this.createProductService = createProductService;
        this.findProductService = findProductService;
        this.listProductsService = listProductsService;
        this.updateProductService = updateProductService;
        this.deleteProductService = deleteProductService;
        this.stockMovementService = stockMovementService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return createdResponse(createProductService.create(request));
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

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(findProductService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "A página deve ser maior ou igual a 1") int page,
            @RequestParam(name = "size", defaultValue = "20")
            @Min(value = 1, message = "O tamanho da página deve ser maior ou igual a 1") int size
    ) {
        PageRequest pageable = PageRequest.of(
                page - 1,
                Math.min(size, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.ASC, "name")
        );
        return ResponseEntity.ok(listProductsService.findAll(pageable));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(updateProductService.update(id, request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateWithImage(
            @PathVariable Long id,
            @Valid @RequestPart("product") UpdateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(updateProductService.update(id, request, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stock-movements")
    public ResponseEntity<Void> moveStock(
            @PathVariable Long id,
            @Valid @RequestBody StockMovementRequest request
    ) {
        stockMovementService.stockMoviment(id, request);
        return ResponseEntity.noContent().build();
    }
}
