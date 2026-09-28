package br.edu.utfpr.apiWebStore.email.infraestructure.mapper;

import br.edu.utfpr.apiWebStore.email.api.request.InvoiceProductRequest;
import br.edu.utfpr.apiWebStore.email.api.request.InvoiceRequest;
import br.edu.utfpr.apiWebStore.invoice.api.response.InvoiceProductResponse;
import br.edu.utfpr.apiWebStore.invoice.api.response.InvoiceResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceEmailMapper {

    public InvoiceRequest toRequest(InvoiceResponse response) {
        List<InvoiceProductRequest> products = response.products()
                .stream()
                .map(this::toProductRequest)
                .toList();

        return new InvoiceRequest(
                response.id(),
                response.customer(),
                response.customerCpf(),
                response.customerEmail(),
                response.customerCelphone(),
                response.customerAddressStreet(),
                response.customerAddressNumber(),
                response.customerAddressNeighborhood(),
                response.customerAddressCep(),
                response.customerAddressCity(),
                response.customerAddressState(),
                response.customerAddressComplement(),
                response.purchaseId(),
                response.createdAt(),
                response.canceledAt(),
                response.status().name(),
                products
        );
    }

    private InvoiceProductRequest toProductRequest(InvoiceProductResponse response) {
        return new InvoiceProductRequest(
                response.id(),
                response.productId(),
                response.productQuantity(),
                response.productPrice(),
                response.totalAmount()
        );
    }
}
