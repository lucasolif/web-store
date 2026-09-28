package br.edu.utfpr.apiWebStore.invoice.infraestructure.mapper;

import br.edu.utfpr.apiWebStore.buy.domain.Buyer;
import br.edu.utfpr.apiWebStore.buy.domain.DeliveryAddress;
import br.edu.utfpr.apiWebStore.buy.domain.Purchase;
import br.edu.utfpr.apiWebStore.buy.domain.PurchaseItem;
import br.edu.utfpr.apiWebStore.invoice.api.request.InvoiceProductRequest;
import br.edu.utfpr.apiWebStore.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiWebStore.invoice.domain.enumeration.InvoiceStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceMapper {

    public InvoiceRequest toRequest(
            Purchase purchase,
            Buyer buyer,
            DeliveryAddress address,
            List<PurchaseItem> purchaseItems
    ) {
        List<InvoiceProductRequest> products = purchaseItems.stream()
                .map(item -> new InvoiceProductRequest(
                        item.getProductId(),
                        null,
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalAmount()
                ))
                .toList();

        return new InvoiceRequest(
                buyer.getName(),
                buyer.getCpf(),
                buyer.getEmail(),
                buyer.getCellphone(),
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getCep(),
                address.getCity(),
                address.getState(),
                address.getComplement(),
                purchase.getId(),
                purchase.getPurchaseDate(),
                null,
                InvoiceStatus.ACTIVE,
                products
        );
    }
}
