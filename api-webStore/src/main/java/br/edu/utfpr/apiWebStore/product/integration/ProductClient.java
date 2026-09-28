package br.edu.utfpr.apiWebStore.product.integration;

import br.edu.utfpr.apiWebStore.product.api.request.CreateProductRequest;
import br.edu.utfpr.apiWebStore.product.api.request.StockMovementRequest;
import br.edu.utfpr.apiWebStore.product.api.request.UpdateProductRequest;
import br.edu.utfpr.apiWebStore.product.api.response.ProductResponse;
import br.edu.utfpr.apiWebStore.product.application.ProductImageContent;
import br.edu.utfpr.apiWebStore.shared.exception.ConflictException;
import br.edu.utfpr.apiWebStore.shared.exception.ExternalServiceException;
import br.edu.utfpr.apiWebStore.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apiWebStore.shared.exception.ValidationException;
import br.edu.utfpr.apiWebStore.shared.exception.web.CorrelationIdFilter;
import br.edu.utfpr.apiWebStore.shared.response.PageResponse;
import br.edu.utfpr.apiWebStore.shared.response.PaginationResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
public class ProductClient {

    private final RestClient restClient;
    private final String productsPath;
    private final String productByIdPath;
    private final String productImagePath;
    private final String stockMovementPath;

    public ProductClient(
            @Value("${integrations.products.base-url}") String baseUrl,
            @Value("${integrations.products.connect-timeout:3s}") Duration connectTimeout,
            @Value("${integrations.products.read-timeout:10s}") Duration readTimeout,
            @Value("${integrations.products.routes.products}") String productsPath,
            @Value("${integrations.products.routes.product-by-id}") String productByIdPath,
            @Value("${integrations.products.routes.product-image}") String productImagePath,
            @Value("${integrations.products.routes.stock-movement}") String stockMovementPath
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    String correlationId = MDC.get("correlationId");
                    if (correlationId != null && !correlationId.isBlank()) {
                        request.getHeaders().set(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
                    }
                    return execution.execute(request, body);
                })
                .build();

        this.productsPath = productsPath;
        this.productByIdPath = productByIdPath;
        this.productImagePath = productImagePath;
        this.stockMovementPath = stockMovementPath;
    }

    public PageResponse<ProductResponse> findAll(int page, int size) {
        try {
            ProductPageResult result = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(productsPath)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .retrieve()
                    .body(ProductPageResult.class);

            if (result == null || result.data() == null || result.pagination() == null) {
                throw new ExternalServiceException(
                        "INVALID_PRODUCT_API_RESPONSE",
                        "A API de produtos retornou uma resposta inválida."
                );
            }

            return new PageResponse<>(result.data(), result.pagination());
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível consultar os produtos.",
                    exception
            );
        }
    }

    public ProductResponse findById(Long id) {
        try {
            ProductResult result = restClient.get()
                    .uri(productByIdPath, id)
                    .retrieve()
                    .body(ProductResult.class);

            if (result == null || result.data() == null) {
                throw new ExternalServiceException(
                        "INVALID_PRODUCT_API_RESPONSE",
                        "A API de produtos retornou uma resposta inválida."
                );
            }

            return result.data();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PRODUCT_NOT_FOUND",
                    "Produto não encontrado: " + id
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível consultar o produto: " + id,
                    exception
            );
        }
    }

    public ProductImageContent findImage(String fileName) {
        try {
            ResponseEntity<byte[]> response = restClient.get()
                    .uri(productImagePath, fileName)
                    .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.parseMediaType("image/webp"))
                    .retrieve()
                    .toEntity(byte[].class);

            byte[] content = response.getBody();
            if (content == null || content.length == 0) {
                throw new ResourceNotFoundException(
                        "PRODUCT_IMAGE_NOT_FOUND",
                        "Imagem do produto não encontrada: " + fileName
                );
            }

            MediaType contentType = response.getHeaders().getContentType();
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }

            return new ProductImageContent(content, contentType, response.getHeaders().getCacheControl());
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PRODUCT_IMAGE_NOT_FOUND",
                    "Imagem do produto não encontrada: " + fileName
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível consultar a imagem do produto.",
                    exception
            );
        }
    }

    public ProductResponse create(CreateProductRequest request) {
        try {
            ProductResult result = restClient.post()
                    .uri(productsPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ProductResult.class);

            if (result == null || result.data() == null) {
                throw new ExternalServiceException(
                        "INVALID_PRODUCT_API_RESPONSE",
                        "A API de produtos retornou uma resposta inválida."
                );
            }

            return result.data();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_PRODUCT",
                    "Os dados informados para o produto são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PRODUCT_CONFLICT",
                    "Não foi possível cadastrar o produto devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível cadastrar o produto.",
                    exception
            );
        }
    }

    public ProductResponse createWithImage(CreateProductRequest request, MultipartFile image) {
        try {
            ProductResult result = restClient.post()
                    .uri(productsPath)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(createMultipartBody(request, image))
                    .retrieve()
                    .body(ProductResult.class);

            if (result == null || result.data() == null) {
                throw new ExternalServiceException(
                        "INVALID_PRODUCT_API_RESPONSE",
                        "A API de produtos retornou uma resposta inválida."
                );
            }

            return result.data();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_PRODUCT",
                    "Os dados informados para o produto são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PRODUCT_CONFLICT",
                    "Não foi possível cadastrar o produto devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível cadastrar o produto.",
                    exception
            );
        }
    }

    public ProductResponse update(Long id, UpdateProductRequest request) {
        try {
            ProductResult result = restClient.put()
                    .uri(productByIdPath, id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ProductResult.class);

            if (result == null || result.data() == null) {
                throw new ExternalServiceException(
                        "INVALID_PRODUCT_API_RESPONSE",
                        "A API de produtos retornou uma resposta inválida."
                );
            }

            return result.data();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PRODUCT_NOT_FOUND",
                    "Produto não encontrado: " + id
            );
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_PRODUCT",
                    "Os dados informados para o produto são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PRODUCT_CONFLICT",
                    "Não foi possível alterar o produto devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível alterar o produto: " + id,
                    exception
            );
        }
    }

    public ProductResponse updateWithImage(Long id, UpdateProductRequest request, MultipartFile image) {
        try {
            ProductResult result = restClient.put()
                    .uri(productByIdPath, id)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(createMultipartBody(request, image))
                    .retrieve()
                    .body(ProductResult.class);

            if (result == null || result.data() == null) {
                throw new ExternalServiceException(
                        "INVALID_PRODUCT_API_RESPONSE",
                        "A API de produtos retornou uma resposta inválida."
                );
            }

            return result.data();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PRODUCT_NOT_FOUND",
                    "Produto não encontrado: " + id
            );
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_PRODUCT",
                    "Os dados informados para o produto são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PRODUCT_CONFLICT",
                    "Não foi possível alterar o produto devido a um conflito."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível alterar o produto: " + id,
                    exception
            );
        }
    }

    public void delete(Long id) {
        try {
            restClient.delete()
                    .uri(productByIdPath, id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PRODUCT_NOT_FOUND",
                    "Produto não encontrado: " + id
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "PRODUCT_DELETE_CONFLICT",
                    "O produto não pode ser excluído porque está em uso."
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível excluir o produto: " + id,
                    exception
            );
        }
    }

    public void moveStock(Long productId, StockMovementRequest request) {
        try {
            restClient.post()
                    .uri(stockMovementPath, productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "PRODUCT_NOT_FOUND",
                    "Produto não encontrado: " + productId
            );
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new ValidationException(
                    "INVALID_STOCK_MOVEMENT",
                    "Os dados informados para a movimentação de estoque são inválidos."
            );
        } catch (HttpClientErrorException.Conflict exception) {
            throw new ConflictException(
                    "INSUFFICIENT_STOCK",
                    "Estoque insuficiente para o produto: " + productId
            );
        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "PRODUCT_API_UNAVAILABLE",
                    "Não foi possível atualizar o estoque do produto: " + productId,
                    exception
            );
        }
    }

    private MultiValueMap<String, Object> createMultipartBody(Object product, MultipartFile image) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        HttpHeaders productHeaders = new HttpHeaders();
        productHeaders.setContentType(MediaType.APPLICATION_JSON);
        parts.add("product", new HttpEntity<>(product, productHeaders));

        if (image != null && !image.isEmpty()) {
            parts.add("image", createImagePart(image));
        }

        return parts;
    }

    private HttpEntity<ByteArrayResource> createImagePart(MultipartFile image) {
        try {
            ByteArrayResource resource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(image.getContentType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(image.getContentType()));

            return new HttpEntity<>(resource, headers);
        } catch (IOException exception) {
            throw new ValidationException(
                    "INVALID_PRODUCT_IMAGE",
                    "Não foi possível ler a imagem enviada."
            );
        }
    }

    private record ProductResult(ProductResponse data) {
    }

    private record ProductPageResult(
            List<ProductResponse> data,
            PaginationResponse pagination
    ) {
    }
}
