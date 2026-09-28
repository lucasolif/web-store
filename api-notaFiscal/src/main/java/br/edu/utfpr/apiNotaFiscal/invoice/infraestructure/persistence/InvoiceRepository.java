package br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.persistence;

import br.edu.utfpr.apiNotaFiscal.invoice.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
