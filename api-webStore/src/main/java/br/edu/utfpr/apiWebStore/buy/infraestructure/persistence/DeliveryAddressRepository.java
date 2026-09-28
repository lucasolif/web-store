package br.edu.utfpr.apiWebStore.buy.infraestructure.persistence;

import br.edu.utfpr.apiWebStore.buy.domain.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    Optional<DeliveryAddress> findByPurchaseId_Id(Long purchaseId);
}
