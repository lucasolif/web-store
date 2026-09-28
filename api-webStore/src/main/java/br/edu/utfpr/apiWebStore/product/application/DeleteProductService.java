package br.edu.utfpr.apiWebStore.product.application;

import br.edu.utfpr.apiWebStore.product.integration.ProductClient;
import org.springframework.stereotype.Service;

@Service
public class DeleteProductService {

    private final ProductClient productClient;

    public DeleteProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public void delete(Long id) {
        productClient.delete(id);
    }
}
