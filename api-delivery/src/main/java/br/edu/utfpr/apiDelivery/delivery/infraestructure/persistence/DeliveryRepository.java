package br.edu.utfpr.apiDelivery.delivery.infraestructure.persistence;

import br.edu.utfpr.apiDelivery.delivery.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsByTrackingCode(String trackingCode);
}
