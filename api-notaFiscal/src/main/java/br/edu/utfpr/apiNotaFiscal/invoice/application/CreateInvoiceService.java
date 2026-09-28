package br.edu.utfpr.apiNotaFiscal.invoice.application;

import br.edu.utfpr.apiNotaFiscal.invoice.api.request.InvoiceProductRequest;
import br.edu.utfpr.apiNotaFiscal.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiNotaFiscal.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiNotaFiscal.invoice.domain.Invoice;
import br.edu.utfpr.apiNotaFiscal.invoice.domain.InvoiceProduct;
import br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.mapper.InvoiceMapper;
import br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.persistence.InvoiceProductRepository;
import br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.persistence.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreateInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceMapper invoiceMapper;

    public CreateInvoiceService(
            InvoiceRepository invoiceRepository,
            InvoiceProductRepository invoiceProductRepository,
            InvoiceMapper invoiceMapper
    ){
        this.invoiceRepository = invoiceRepository;
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceRequest);
        Invoice invoiceCreated = invoiceRepository.save(invoice);

        List<InvoiceProduct> products = invoiceMapper.toInvoiceProducts(invoiceRequest.products(), invoiceCreated);
        List<InvoiceProduct> savedProducts = invoiceProductRepository.saveAll(products);

        return invoiceMapper.toResponse(invoiceCreated, savedProducts);
    }

}
