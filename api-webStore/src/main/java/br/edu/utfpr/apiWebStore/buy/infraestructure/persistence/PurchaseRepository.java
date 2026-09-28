package br.edu.utfpr.apiWebStore.buy.infraestructure.persistence;

import br.edu.utfpr.apiWebStore.buy.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
