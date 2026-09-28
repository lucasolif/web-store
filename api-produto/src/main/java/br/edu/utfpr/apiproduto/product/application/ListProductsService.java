package br.edu.utfpr.apiproduto.product.application;

import br.edu.utfpr.apiproduto.product.api.response.ProductResponse;
import br.edu.utfpr.apiproduto.product.infrastructure.mapper.ProductMapper;
import br.edu.utfpr.apiproduto.product.infrastructure.persistence.ProductRepository;
import br.edu.utfpr.apiproduto.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListProductsService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ListProductsService(
            ProductRepository productRepository,
            ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(Pageable pageable) {
        return PageResponse.of(
                productRepository.findAll(pageable).map(productMapper::toResponse)
        );
    }
}
