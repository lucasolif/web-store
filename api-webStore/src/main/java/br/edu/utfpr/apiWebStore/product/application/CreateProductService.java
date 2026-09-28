package br.edu.utfpr.apiWebStore.product.application;

import br.edu.utfpr.apiWebStore.product.api.request.CreateProductRequest;
import br.edu.utfpr.apiWebStore.product.api.response.ProductResponse;
import br.edu.utfpr.apiWebStore.product.integration.ProductClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CreateProductService {

    private final ProductClient productClient;

    public CreateProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public ProductResponse create(CreateProductRequest request, MultipartFile image) {
        return image == null ? productClient.create(request) : productClient.createWithImage(request, image);
    }
}
