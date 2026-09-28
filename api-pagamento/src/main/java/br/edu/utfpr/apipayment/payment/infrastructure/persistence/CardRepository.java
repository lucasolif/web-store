package br.edu.utfpr.apipayment.payment.infrastructure.persistence;

import br.edu.utfpr.apipayment.payment.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByPaymentId(Long paymentId);
}
