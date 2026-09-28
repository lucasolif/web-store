package br.edu.utfpr.apiDelivery.delivery.infraestructure.persistence;

import br.edu.utfpr.apiDelivery.delivery.domain.DeliveryProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryProductRepository extends JpaRepository<DeliveryProduct, Long> {
}
