package br.edu.utfpr.apiproduto.product.application;

import br.edu.utfpr.apiproduto.product.api.request.UpdateProductRequest;
import br.edu.utfpr.apiproduto.product.api.response.ProductResponse;
import br.edu.utfpr.apiproduto.product.domain.Product;
import br.edu.utfpr.apiproduto.product.infrastructure.mapper.ProductMapper;
import br.edu.utfpr.apiproduto.product.infrastructure.persistence.ProductRepository;
import br.edu.utfpr.apiproduto.product.infrastructure.storage.ProductImageStorage;
import br.edu.utfpr.apiproduto.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class UpdateProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageStorage productImageStorage;

    public UpdateProductService(
            ProductRepository productRepository,
            ProductMapper productMapper,
            ProductImageStorage productImageStorage
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productImageStorage = productImageStorage;
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        return update(id, request, null);
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PRODUCT_NOT_FOUND", "Produto não encontrado: " + id
                ));

        product.setName(productMapper.normalize(request.name()));
        product.setDescription(productMapper.normalizeOptional(request.description()));
        product.setPrice(request.price());
        product.setUpdatedAt(LocalDateTime.now());

        if (image != null) {
            String previousImagePath = product.getImagePath();
            String newImagePath = productImageStorage.store(image);
            product.setImagePath(newImagePath);
            productImageStorage.deleteOnTransactionRollback(newImagePath);
            productImageStorage.deleteAfterTransactionCommit(previousImagePath);
        }

        Product productUpdated = productRepository.saveAndFlush(product);

        return productMapper.toResponse(productUpdated);
    }
}
