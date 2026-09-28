package br.edu.utfpr.apiWebStore.buy.infraestructure.mapper;

import br.edu.utfpr.apiWebStore.buy.api.request.*;
import br.edu.utfpr.apiWebStore.buy.domain.Buyer;
import br.edu.utfpr.apiWebStore.buy.domain.DeliveryAddress;
import br.edu.utfpr.apiWebStore.buy.domain.Purchase;
import br.edu.utfpr.apiWebStore.buy.domain.PurchaseItem;
import br.edu.utfpr.apiWebStore.buy.domain.enumeration.PurchaseStatus;
import br.edu.utfpr.apiWebStore.product.api.response.ProductResponse;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PurchaseMapper {

    public DeliveryAddress toDeliveryAddress(DeliveryAddressRequest deliveryRequest){

        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setCep(deliveryRequest.cep());
        deliveryAddress.setCity(deliveryRequest.city());
        deliveryAddress.setComplement(deliveryRequest.complement());
        deliveryAddress.setNumber(deliveryRequest.number());
        deliveryAddress.setNeighborhood( deliveryRequest.neighborhood());
        deliveryAddress.setStreet(deliveryRequest.street());
        deliveryAddress.setState(deliveryRequest.state());

        return deliveryAddress;

    }

    public Buyer toBuyer(CreateBuyerRequest createBuyerRequest){

        Buyer buyer = new Buyer();
        buyer.setCellphone(createBuyerRequest.cellphone());
        buyer.setCpf(createBuyerRequest.cpf());
        buyer.setEmail(createBuyerRequest.email());
        buyer.setName(createBuyerRequest.name());

        return buyer;
    }

    public Purchase toPurchase(CreatePurchaseRequest purchaseRequest){
        Purchase purchase = new Purchase();
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setBuyTotalValue(purchaseRequest.totalAmount());

        return purchase;
    }

    public PurchaseItem toPurchaseItem(
            CreatePurchaseItemRequest itemRequest,
            ProductResponse product,
            Purchase purchase
    ) {
        PurchaseItem purchaseItem = new PurchaseItem();
        purchaseItem.setProductId(product.id());
        purchaseItem.setProductName(product.name());
        purchaseItem.setUnitPrice(product.price());
        purchaseItem.setQuantity(itemRequest.quantity());
        purchaseItem.setTotalAmount(product.price().multiply(itemRequest.quantity()));
        purchaseItem.setPurchase(purchase);
        return purchaseItem;
    }

}
