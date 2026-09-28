package br.edu.utfpr.apipayment.payment.infrastructure.persistence;

import br.edu.utfpr.apipayment.payment.domain.BankSlip;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.domain.enumeration.BankSlipStatus;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankSlipRepository extends JpaRepository<BankSlip, Long> {
    Optional<BankSlip> findByPaymentId(Long paymentId);
    Optional<BankSlip> findByPaymentIdAndStatus(Long paymentId, BankSlipStatus bankSlipStatus);
    Optional<BankSlip> findByIdAndStatus(Long paymentId, BankSlipStatus bankSlipStatus);
    boolean existsByBarcode(String barcode);
}
