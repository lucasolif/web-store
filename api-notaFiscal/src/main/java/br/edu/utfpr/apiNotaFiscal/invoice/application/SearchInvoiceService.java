package br.edu.utfpr.apiNotaFiscal.invoice.application;

import br.edu.utfpr.apiNotaFiscal.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiNotaFiscal.invoice.domain.Invoice;
import br.edu.utfpr.apiNotaFiscal.invoice.domain.InvoiceProduct;
import br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.mapper.InvoiceMapper;
import br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.persistence.InvoiceProductRepository;
import br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.persistence.InvoiceRepository;
import br.edu.utfpr.apiNotaFiscal.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceMapper invoiceMapper;

    public SearchInvoiceService(
            InvoiceRepository invoiceRepository,
            InvoiceProductRepository invoiceProductRepository,
            InvoiceMapper invoiceMapper
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "INVOICE_NOT_FOUND",
                        "Nota fiscal não encontrada."
                ));

        List<InvoiceProduct> products = invoiceProductRepository.findAllByInvoice_Id(invoiceId);
        return invoiceMapper.toResponse(invoice, products);
    }
}
