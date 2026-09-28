package br.edu.utfpr.apiDelivery.delivery.infraestructure.mapper;

import br.edu.utfpr.apiDelivery.delivery.api.request.DeliveryProductsRequest;
import br.edu.utfpr.apiDelivery.delivery.api.request.DeliveryRequest;
import br.edu.utfpr.apiDelivery.delivery.api.response.DeliveryResponse;
import br.edu.utfpr.apiDelivery.delivery.domain.Delivery;
import br.edu.utfpr.apiDelivery.delivery.domain.DeliveryProduct;
import br.edu.utfpr.apiDelivery.delivery.domain.enumeration.DeliveryStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DeliveryMapper {

    public DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getTrackingCode(),
                delivery.getDeliveryPrevision(),
                delivery.getCustomer(),
                delivery.getCustomerCpf(),
                delivery.getCustomerEmail(),
                delivery.getCustomerAddressStreet(),
                delivery.getCustomerAddressNumber(),
                delivery.getCustomerAddressNeighborhood(),
                delivery.getCustomerAddressCep(),
                delivery.getCustomerAddressCity(),
                delivery.getCustomerAddressState(),
                delivery.getCustomerAddressComplement()
        );
    }

    public Delivery toDelivery(DeliveryRequest request) {
        Delivery delivery = new Delivery();

        delivery.setCustomer(request.customer());
        delivery.setCustomerCpf(request.customerCpf());
        delivery.setCustomerEmail(request.customerEmail());
        delivery.setCustomerCelphone(request.customerCelphone());

        delivery.setCustomerAddressStreet(request.customerAddressStreet());
        delivery.setCustomerAddressNumber(request.customerAddressNumber());
        delivery.setCustomerAddressNeighborhood(request.customerAddressNeighborhood());
        delivery.setCustomerAddressCep(request.customerAddressCep());
        delivery.setCustomerAddressCity(request.customerAddressCity());
        delivery.setCustomerAddressState(request.customerAddressState());
        delivery.setCustomerAddressComplement(request.customerAddressComplement());

        delivery.setPurchaseId(request.purchaseId());
        delivery.setCreatedAt(request.createdAt());
        delivery.setDeliveryCreatedAt(LocalDate.now());
        delivery.setDeliveryPrevision(LocalDate.now().plusDays(7));
        delivery.setStatus(DeliveryStatus.PENDING);

        for (DeliveryProductsRequest productRequest : request.products()) {
            DeliveryProduct product = toDeliveryProduct(productRequest);
            product.setDelivery(delivery);
            delivery.getProducts().add(product);
        }

        return delivery;
    }

    private DeliveryProduct toDeliveryProduct(DeliveryProductsRequest request) {
        DeliveryProduct product = new DeliveryProduct();

        product.setProdutoId(request.produtoId());
        product.setInvoiceId(request.invoiceId());
        product.setProductQuantity(request.productQuantity());
        product.setProductPrice(request.productPrice());
        product.setTotalAmount(request.totalAmount());

        return product;
    }
}