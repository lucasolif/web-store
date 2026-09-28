package br.edu.utfpr.apiproduto.product.infrastructure.mapper;

import br.edu.utfpr.apiproduto.product.api.request.CreateProductRequest;
import br.edu.utfpr.apiproduto.product.api.response.ProductResponse;
import br.edu.utfpr.apiproduto.product.domain.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        return new Product(
                normalize(request.name()),
                normalizeOptional(request.description()),
                request.price()
        );
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImagePath(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public String normalize(String value) {
        return value.trim();
    }

    public String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
