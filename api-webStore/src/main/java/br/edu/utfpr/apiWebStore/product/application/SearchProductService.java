package br.edu.utfpr.apiWebStore.product.application;

import br.edu.utfpr.apiWebStore.product.api.response.ProductResponse;
import br.edu.utfpr.apiWebStore.product.integration.ProductClient;
import br.edu.utfpr.apiWebStore.shared.response.PageResponse;
import org.springframework.stereotype.Service;

@Service
public class SearchProductService {

    private final ProductClient productClient;

    public SearchProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public PageResponse<ProductResponse> findAll(int page, int size) {
        return productClient.findAll(page, size);
    }

    public ProductResponse findById(Long id) {
        return productClient.findById(id);
    }

    public ProductImageContent findImage(String fileName) {
        return productClient.findImage(fileName);
    }
}
