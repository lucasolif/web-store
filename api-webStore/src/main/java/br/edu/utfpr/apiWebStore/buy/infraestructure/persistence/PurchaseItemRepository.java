package br.edu.utfpr.apiWebStore.buy.infraestructure.persistence;

import br.edu.utfpr.apiWebStore.buy.domain.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
    List<PurchaseItem> findAllByPurchase_Id(Long purchaseId);
    void deleteAllByPurchase_Id(Long purchaseId);
}
