package br.edu.utfpr.apiWebStore.product.application;

import br.edu.utfpr.apiWebStore.product.api.request.StockMovementRequest;
import br.edu.utfpr.apiWebStore.product.integration.ProductClient;
import org.springframework.stereotype.Service;

@Service
public class StockMovementService {

    private final ProductClient productClient;

    public StockMovementService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public void moveStock(Long productId, StockMovementRequest request) {
        productClient.moveStock(productId, request);
    }
}
