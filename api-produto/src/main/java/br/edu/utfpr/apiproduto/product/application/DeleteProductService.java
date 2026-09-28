package br.edu.utfpr.apiproduto.product.application;

import br.edu.utfpr.apiproduto.product.domain.Product;
import br.edu.utfpr.apiproduto.product.infrastructure.persistence.ProductRepository;
import br.edu.utfpr.apiproduto.product.infrastructure.storage.ProductImageStorage;
import br.edu.utfpr.apiproduto.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteProductService {

    private final ProductRepository productRepository;
    private final ProductImageStorage productImageStorage;

    public DeleteProductService(
            ProductRepository productRepository,
            ProductImageStorage productImageStorage
    ) {
        this.productRepository = productRepository;
        this.productImageStorage = productImageStorage;
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "PRODUCT_NOT_FOUND",
                    "Produto não encontrado: " + id
            ));
        productRepository.delete(product);
        productRepository.flush();
        productImageStorage.deleteAfterTransactionCommit(product.getImagePath());
    }
}
