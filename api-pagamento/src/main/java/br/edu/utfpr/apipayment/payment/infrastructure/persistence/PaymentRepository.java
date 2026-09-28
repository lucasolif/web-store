package br.edu.utfpr.apipayment.payment.infrastructure.persistence;


import br.edu.utfpr.apipayment.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
