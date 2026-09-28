package br.edu.utfpr.apipayment.payment.infrastructure.persistence;

import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PixRepository extends JpaRepository<Pix, Long> {
    Optional<Pix> findByPaymentIdAndStatusNot(Long paymentId, PixStatus pixStatus);
    Optional<Pix> findByIdAndStatus(Long paymentId, PixStatus pixStatus);
    Optional<Pix> findByPaymentId(Long paymentId);
    boolean existsByQrCode(String qrCode);
}
