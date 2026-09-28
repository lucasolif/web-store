package br.edu.utfpr.apiWebStore.invoice.application;

import br.edu.utfpr.apiWebStore.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiWebStore.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiWebStore.invoice.integration.InvoiceClient;
import org.springframework.stereotype.Service;

@Service
public class CreateInvoiceService {

    private final InvoiceClient invoiceClient;

    public CreateInvoiceService(InvoiceClient invoiceClient) {
        this.invoiceClient = invoiceClient;
    }

    public InvoiceResponse create(InvoiceRequest request) {
        return invoiceClient.create(request);
    }
}
