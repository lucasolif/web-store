package br.edu.utfpr.apiproduto.product.application;

import br.edu.utfpr.apiproduto.product.api.request.CreateProductRequest;
import br.edu.utfpr.apiproduto.product.api.response.ProductResponse;
import br.edu.utfpr.apiproduto.product.domain.Product;
import br.edu.utfpr.apiproduto.product.infrastructure.mapper.ProductMapper;
import br.edu.utfpr.apiproduto.product.infrastructure.persistence.ProductRepository;
import br.edu.utfpr.apiproduto.product.infrastructure.storage.ProductImageStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class CreateProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageStorage productImageStorage;

    public CreateProductService(
            ProductRepository productRepository,
            ProductMapper productMapper,
            ProductImageStorage productImageStorage
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productImageStorage = productImageStorage;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        return create(request, null);
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request, MultipartFile image) {
        Product product = productMapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);

        if (image != null) {
            String imagePath = productImageStorage.store(image);
            product.setImagePath(imagePath);
            productImageStorage.deleteOnTransactionRollback(imagePath);
        }

        Product productSaved = productRepository.saveAndFlush(product);

        return productMapper.toResponse(productSaved);
    }
}
