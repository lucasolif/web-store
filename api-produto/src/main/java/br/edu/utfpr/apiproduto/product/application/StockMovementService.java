package br.edu.utfpr.apiproduto.product.application;

import br.edu.utfpr.apiproduto.product.api.request.StockMovementRequest;
import br.edu.utfpr.apiproduto.product.domain.Product;
import br.edu.utfpr.apiproduto.product.domain.enumeration.StockMovementType;
import br.edu.utfpr.apiproduto.product.infrastructure.persistence.ProductRepository;
import br.edu.utfpr.apiproduto.shared.exception.ConflictException;
import br.edu.utfpr.apiproduto.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apiproduto.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class StockMovementService {

    private final ProductRepository productRepository;

    public StockMovementService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void stockMoviment(Long id, StockMovementRequest mvRequest) {
        if (mvRequest.quantity() == null || mvRequest.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(
                    "INVALID_QUANTITY",
                    "A quantidade deve ser maior que zero"
            );
        }

        if (mvRequest.operation() == null) {
            throw new ValidationException(
                    "INVALID_OPERATION",
                    "A operação é obrigatória"
            );
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PRODUCT_NOT_FOUND",
                        "Produto não encontrado: " + id
                ));

        if (mvRequest.operation() == StockMovementType.OUTBOUND
                && mvRequest.quantity().compareTo(product.getStockQuantity()) > 0) {
            throw new ConflictException(
                    "INSUFFICIENT_STOCK",
                    "Estoque insuficiente para o produto: " + id
            );
        }

        if (mvRequest.operation() == StockMovementType.OUTBOUND) {
            product.setStockQuantity(product.getStockQuantity().subtract(mvRequest.quantity()));
        } else {
            product.setStockQuantity(product.getStockQuantity().add(mvRequest.quantity()));
        }
    }
}
