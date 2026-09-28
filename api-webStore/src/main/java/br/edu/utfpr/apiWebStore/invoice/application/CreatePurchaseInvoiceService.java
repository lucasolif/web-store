package br.edu.utfpr.apiWebStore.invoice.application;

import br.edu.utfpr.apiWebStore.buy.domain.Buyer;
import br.edu.utfpr.apiWebStore.buy.domain.DeliveryAddress;
import br.edu.utfpr.apiWebStore.buy.domain.Purchase;
import br.edu.utfpr.apiWebStore.buy.domain.PurchaseItem;
import br.edu.utfpr.apiWebStore.buy.domain.enumeration.PurchaseStatus;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.BuyerRepository;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.DeliveryAddressRepository;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.PurchaseItemRepository;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.PurchaseRepository;
import br.edu.utfpr.apiWebStore.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiWebStore.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiWebStore.invoice.infraestructure.mapper.InvoiceMapper;
import br.edu.utfpr.apiWebStore.invoice.integration.InvoiceClient;
import br.edu.utfpr.apiWebStore.product.api.request.StockMovementRequest;
import br.edu.utfpr.apiWebStore.product.application.StockMovementService;
import br.edu.utfpr.apiWebStore.product.domain.enumeration.StockMovementType;
import br.edu.utfpr.apiWebStore.shared.exception.ConflictException;
import br.edu.utfpr.apiWebStore.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apiWebStore.shared.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatePurchaseInvoiceService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final BuyerRepository buyerRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceClient invoiceClient;
    private final StockMovementService stockMovementService;

    public CreatePurchaseInvoiceService(
            PurchaseRepository purchaseRepository,
            PurchaseItemRepository purchaseItemRepository,
            BuyerRepository buyerRepository,
            DeliveryAddressRepository deliveryAddressRepository,
            InvoiceMapper invoiceMapper,
            InvoiceClient invoiceClient,
            StockMovementService stockMovementService
    ) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.buyerRepository = buyerRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.invoiceMapper = invoiceMapper;
        this.invoiceClient = invoiceClient;
        this.stockMovementService = stockMovementService;
    }

    public InvoiceResponse create(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PURCHASE_NOT_FOUND",
                        "Compra não encontrada."
                ));

        if (purchase.getStatus() != PurchaseStatus.COMPLETED) {
            throw new ConflictException(
                    "PURCHASE_NOT_COMPLETED",
                    "A nota fiscal somente pode ser gerada para uma compra confirmada."
            );
        }

        Buyer buyer = buyerRepository.findByPurchaseId_Id(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PURCHASE_BUYER_NOT_FOUND",
                        "Os dados do comprador não foram encontrados."
                ));

        DeliveryAddress address = deliveryAddressRepository.findByPurchaseId_Id(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PURCHASE_ADDRESS_NOT_FOUND",
                        "O endereço da compra não foi encontrado."
                ));

        List<PurchaseItem> purchaseItems = purchaseItemRepository.findAllByPurchase_Id(purchaseId);
        if (purchaseItems.isEmpty()) {
            throw new ValidationException(
                    "PURCHASE_WITHOUT_ITEMS",
                    "A compra não possui produtos para emissão da nota fiscal."
            );
        }

        InvoiceRequest request = invoiceMapper.toRequest(purchase, buyer, address, purchaseItems);
        InvoiceResponse response = invoiceClient.create(request);

        purchaseItems.forEach(item -> stockMovementService.moveStock(
                item.getProductId(),
                new StockMovementRequest(item.getQuantity(), StockMovementType.OUTBOUND)
        ));

        return response;
    }
}
