package br.edu.utfpr.apiWebStore.product.application;

import br.edu.utfpr.apiWebStore.product.api.request.UpdateProductRequest;
import br.edu.utfpr.apiWebStore.product.api.response.ProductResponse;
import br.edu.utfpr.apiWebStore.product.integration.ProductClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UpdateProductService {

    private final ProductClient productClient;

    public UpdateProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public ProductResponse update(Long id, UpdateProductRequest request, MultipartFile image) {
        return image == null
                ? productClient.update(id, request)
                : productClient.updateWithImage(id, request, image);
    }
}
