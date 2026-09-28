package br.edu.utfpr.apiWebStore.buy.infraestructure.persistence;

import br.edu.utfpr.apiWebStore.buy.domain.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByPurchaseId_Id(Long purchaseId);
}
