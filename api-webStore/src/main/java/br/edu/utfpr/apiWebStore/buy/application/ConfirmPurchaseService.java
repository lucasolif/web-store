package br.edu.utfpr.apiWebStore.buy.application;

import br.edu.utfpr.apiWebStore.buy.domain.Purchase;
import br.edu.utfpr.apiWebStore.buy.domain.enumeration.PurchaseStatus;
import br.edu.utfpr.apiWebStore.buy.infraestructure.persistence.PurchaseRepository;
import br.edu.utfpr.apiWebStore.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmPurchaseService {

    private final PurchaseRepository purchaseRepository;

    public ConfirmPurchaseService(PurchaseRepository purchaseRepository){
        this.purchaseRepository = purchaseRepository;
    }

    @Transactional
    public void confirmPurchase(Long id){
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                "PURCHASE_NOT_FOUND", "Compra não encontrada"
        ));

        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchaseRepository.save(purchase);
    }
}
