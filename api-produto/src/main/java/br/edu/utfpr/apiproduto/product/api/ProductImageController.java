package br.edu.utfpr.apiproduto.product.api;

import br.edu.utfpr.apiproduto.product.infrastructure.storage.ProductImageStorage;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("${app.product-images.public-path:/api/product-images}")
public class ProductImageController {

    private final ProductImageStorage productImageStorage;

    public ProductImageController(ProductImageStorage productImageStorage) {
        this.productImageStorage = productImageStorage;
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> findImage(@PathVariable String fileName) {
        Resource image = productImageStorage.load(fileName);
        MediaType mediaType = resolveMediaType(fileName);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .body(image);
    }

    private MediaType resolveMediaType(String fileName) {
        if (fileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (fileName.endsWith(".jpg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (fileName.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
