package br.edu.utfpr.apiWebStore.buy.application;

import br.edu.utfpr.apiWebStore.buy.api.request.*;
import br.edu.utfpr.apiWebStore.buy.api.response.PurchaseResponse;
import br.edu.utfpr.apiWebStore.buy.domain.Buyer;
import br.edu.utfpr.apiWebStore.buy.domain.DeliveryAddress;
import br.edu.utfpr.apiWebStore.buy.domain.Purchase;
import br.edu.utfpr.apiWebStore.buy.domain.PurchaseItem;

import br.edu.utfpr.apiWebStore.buy.infraestructure.mapper.PurchaseMapper;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.BuyerRepository;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.DeliveryAddressRepository;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.PurchaseItemRepository;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.PurchaseRepository;
import br.edu.utfpr.apiWebStore.product.api.response.ProductResponse;
import br.edu.utfpr.apiWebStore.product.application.SearchProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreatePurchaseService {

    private final BuyerRepository buyerRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    private final SearchProductService searchProductService;
    private final PurchaseItemRepository purchaseItemRepository;

    public CreatePurchaseService(
            BuyerRepository buyerRepository,
            DeliveryAddressRepository deliveryAddressRepository,
            PurchaseRepository purchaseRepository,
            PurchaseMapper purchaseMapper,
            SearchProductService searchProductService,
            PurchaseItemRepository purchaseItemRepository

    ){
        this.buyerRepository = buyerRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
        this.searchProductService = searchProductService;
        this.purchaseItemRepository = purchaseItemRepository;
    }

    @Transactional
    public PurchaseResponse createPurchase(CreatePurchaseRequest purchaseRequest){

        this.validateBuyer(purchaseRequest.buyer());
        this.validateDeliveryAddress(purchaseRequest.deliveryAddress());

        Purchase purchase = purchaseMapper.toPurchase(purchaseRequest);
        Purchase purchaseCreated = this.purchaseRepository.save(purchase);

        this.createDeliveryAddress(purchaseRequest.deliveryAddress(), purchaseCreated);
        this.createBuyer(purchaseRequest.buyer(), purchaseCreated);
        this.createPurchaseItems(purchaseRequest.purchaseItems(), purchaseCreated);

        return new PurchaseResponse(purchaseCreated.getId());
    }

    private void createDeliveryAddress(DeliveryAddressRequest deliveryRequest, Purchase purchase){

        DeliveryAddress deliveryAddress = this.purchaseMapper.toDeliveryAddress(deliveryRequest);
        deliveryAddress.setPurchaseId(purchase);

        DeliveryAddress deliveryAddressCreated = this.deliveryAddressRepository.save(deliveryAddress);

    }

    private void createBuyer(CreateBuyerRequest createBuyerRequest, Purchase purchase){

        Buyer buyer = this.purchaseMapper.toBuyer(createBuyerRequest);
        buyer.setPurchaseId(purchase);

        Buyer buyerCreated = this.buyerRepository.save(buyer);

    }

    private void createPurchaseItems(List<CreatePurchaseItemRequest> itemRequests, Purchase purchase){
        List<PurchaseItem> purchaseItems = itemRequests.stream()
                .map(itemRequest -> {
                    ProductResponse product = searchProductService.findById(itemRequest.productId());
                    return purchaseMapper.toPurchaseItem(itemRequest, product, purchase);
                })
                .toList();

        purchaseItemRepository.saveAll(purchaseItems);
    }

    private void validateDeliveryAddress(DeliveryAddressRequest deliveryRequest){

        if (deliveryRequest == null) {
            throw new IllegalArgumentException("O endereço de entrega é obrigatório.");
        }

        if (deliveryRequest.street() == null || deliveryRequest.street().isBlank()) {
            throw new IllegalArgumentException("A rua é obrigatória.");
        }

        if (deliveryRequest.neighborhood() == null || deliveryRequest.neighborhood().isBlank()) {
            throw new IllegalArgumentException("O bairro é obrigatório.");
        }

        if (deliveryRequest.number() == null || deliveryRequest.number().isBlank()) {
            throw new IllegalArgumentException("O número é obrigatório.");
        }

        if (deliveryRequest.cep() == null || deliveryRequest.cep().isBlank()) {
            throw new IllegalArgumentException("O CEP é obrigatório.");
        }

        if (deliveryRequest.city() == null || deliveryRequest.city().isBlank()) {
            throw new IllegalArgumentException("A cidade é obrigatória.");
        }

        if (deliveryRequest.state() == null || deliveryRequest.state().isBlank()) {
            throw new IllegalArgumentException("O estado é obrigatório.");
        }
    }

    private void validateBuyer(CreateBuyerRequest createBuyerRequest){

        if (createBuyerRequest == null) {
            throw new IllegalArgumentException("Os dados do comprador são obrigatórios.");
        }

        if (createBuyerRequest.name() == null || createBuyerRequest.name().isBlank()) {
            throw new IllegalArgumentException("O nome do comprador é obrigatório.");
        }

        if (createBuyerRequest.cpf() == null || createBuyerRequest.cpf().isBlank()) {
            throw new IllegalArgumentException("O CPF do comprador é obrigatório.");
        }

        if (!createBuyerRequest.cpf().matches("\\d{11}")) {
            throw new IllegalArgumentException("O CPF do comprador deve conter exatamente 11 dígitos.");
        }

        if (createBuyerRequest.email() == null || createBuyerRequest.email().isBlank()) {
            throw new IllegalArgumentException("O e-mail do comprador é obrigatório.");
        }

        if (createBuyerRequest.cellphone() == null || createBuyerRequest.cellphone().isBlank()) {
            throw new IllegalArgumentException("O celular do comprador é obrigatório.");
        }

    }

}
