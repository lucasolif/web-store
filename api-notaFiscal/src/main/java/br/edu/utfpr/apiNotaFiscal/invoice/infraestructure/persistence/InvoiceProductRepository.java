package br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.persistence;

import br.edu.utfpr.apiNotaFiscal.invoice.domain.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    List<InvoiceProduct> findAllByInvoice_Id(Long invoiceId);
}
