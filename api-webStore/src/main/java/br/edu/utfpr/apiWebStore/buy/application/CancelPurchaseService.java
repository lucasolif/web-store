package br.edu.utfpr.apiWebStore.buy.application;

import br.edu.utfpr.apiWebStore.buy.domain.Purchase;
import br.edu.utfpr.apiWebStore.buy.domain.enumeration.PurchaseStatus;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.PurchaseItemRepository;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.PurchaseRepository;
import br.edu.utfpr.apiWebStore.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelPurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;

    public CancelPurchaseService(
            PurchaseRepository purchaseRepository,
            PurchaseItemRepository purchaseItemRepository
    ){
        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
    }

    @Transactional
    public void canccelPurchase(Long id){
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PURCHASE_NOT_FOUND", "Compra não encontrada"
                ));

        purchase.setStatus(PurchaseStatus.CANCELED);

        this.cancelPurchaseItem(id);
        this.cancelPurchase(purchase);
    }

    private void cancelPurchaseItem(Long purchaseId){
        this.purchaseItemRepository.deleteAllByPurchase_Id(purchaseId);
    }
    private void cancelPurchase(Purchase purchase){
        this.purchaseRepository.save(purchase);
    }
}
