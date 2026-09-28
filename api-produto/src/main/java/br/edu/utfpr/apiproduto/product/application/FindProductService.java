package br.edu.utfpr.apiproduto.product.application;

import br.edu.utfpr.apiproduto.product.api.response.ProductResponse;
import br.edu.utfpr.apiproduto.product.domain.Product;
import br.edu.utfpr.apiproduto.product.infrastructure.mapper.ProductMapper;
import br.edu.utfpr.apiproduto.product.infrastructure.persistence.ProductRepository;
import br.edu.utfpr.apiproduto.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public FindProductService(
            ProductRepository productRepository,
            ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PRODUCT_NOT_FOUND",
                        "Produto não encontrado: " + id
                ));

        return productMapper.toResponse(product);
    }
}
