package br.edu.utfpr.apiWebStore.product.api;

import br.edu.utfpr.apiWebStore.product.application.ProductImageContent;
import br.edu.utfpr.apiWebStore.product.application.SearchProductService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-images")
@Validated
public class ProductImageController {

    private static final String STORED_FILE_NAME =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp)$";
    private static final String CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";

    private final SearchProductService searchProductService;

    public ProductImageController(SearchProductService searchProductService) {
        this.searchProductService = searchProductService;
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<byte[]> findImage(
            @PathVariable
            @Pattern(regexp = STORED_FILE_NAME, message = "O nome do arquivo de imagem possui formato inválido")
            String fileName
    ) {
        ProductImageContent image = searchProductService.findImage(fileName);
        ResponseEntity.BodyBuilder response = ResponseEntity.ok()
                .contentType(image.mediaType())
                .contentLength(image.content().length)
                .header(CONTENT_TYPE_OPTIONS, "nosniff");

        if (image.cacheControl() != null && !image.cacheControl().isBlank()) {
            response.header(HttpHeaders.CACHE_CONTROL, image.cacheControl());
        }

        return response.body(image.content());
    }
}
