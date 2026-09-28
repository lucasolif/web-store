package br.edu.utfpr.apiNotaFiscal.invoice.infraestructure.mapper;

import br.edu.utfpr.apiNotaFiscal.invoice.api.request.InvoiceProductRequest;
import br.edu.utfpr.apiNotaFiscal.invoice.api.request.InvoiceRequest;
import br.edu.utfpr.apiNotaFiscal.invoice.api.response.InvoiceProductResponse;
import br.edu.utfpr.apiNotaFiscal.invoice.api.response.InvoiceResponse;
import br.edu.utfpr.apiNotaFiscal.invoice.domain.Invoice;
import br.edu.utfpr.apiNotaFiscal.invoice.domain.InvoiceProduct;
import br.edu.utfpr.apiNotaFiscal.invoice.domain.enumeration.InvoiceStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class InvoiceMapper {

    public Invoice toInvoice(InvoiceRequest invoiceRequest){

        Invoice invoice = new Invoice();
        invoice.setCustomer(invoiceRequest.customer());
        invoice.setCustomerCpf(invoiceRequest.customerCpf());
        invoice.setCustomerEmail(invoiceRequest.customerEmail());
        invoice.setCustomerCelphone(invoiceRequest.customerCelphone());
        invoice.setCustomerAddressStreet(invoiceRequest.customerAddressStreet());
        invoice.setCustomerAddressNumber(invoiceRequest.customerAddressNumber());
        invoice.setCustomerAddressNeighborhood(invoiceRequest.customerAddressNeighborhood());
        invoice.setCustomerAddressCep(invoiceRequest.customerAddressCep());
        invoice.setCustomerAddressCity(invoiceRequest.customerAddressCity());
        invoice.setCustomerAddressState(invoiceRequest.customerAddressState());
        invoice.setCustomerAddressComplement(invoiceRequest.customerAddressComplement());
        invoice.setCreatedAt(LocalDate.now());
        invoice.setStatus(InvoiceStatus.ACTIVE);
        invoice.setPurchaseId(invoiceRequest.purchaseId());

        return invoice;
    }

    public List<InvoiceProduct> toInvoiceProducts(
            List<InvoiceProductRequest> productRequests,
            Invoice invoice
    ) {
        return productRequests.stream()
                .map(productRequest -> {
                    InvoiceProduct invoiceProduct = new InvoiceProduct();
                    invoiceProduct.setInvoice(invoice);
                    invoiceProduct.setProductId(productRequest.produtoId());
                    invoiceProduct.setProductPrice(productRequest.productPrice());
                    invoiceProduct.setProductQuantity(productRequest.productQuantity());
                    invoiceProduct.setTotalAmount(productRequest.totalAmount());
                    return invoiceProduct;
                })
                .toList();
    }

    public InvoiceResponse toResponse(Invoice invoice, List<InvoiceProduct> products) {

        List<InvoiceProductResponse> productResponses = products.stream()
                .map(product -> new InvoiceProductResponse(
                        product.getId(),
                        product.getProductId(),
                        product.getProductQuantity(),
                        product.getProductPrice(),
                        product.getTotalAmount()
                ))
                .toList();

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getCustomer(),
                invoice.getCustomerCpf(),
                invoice.getCustomerEmail(),
                invoice.getCustomerCelphone(),
                invoice.getCustomerAddressStreet(),
                invoice.getCustomerAddressNumber(),
                invoice.getCustomerAddressNeighborhood(),
                invoice.getCustomerAddressCep(),
                invoice.getCustomerAddressCity(),
                invoice.getCustomerAddressState(),
                invoice.getCustomerAddressComplement(),
                invoice.getPurchaseId(),
                invoice.getCreatedAt(),
                invoice.getCanceledAt(),
                invoice.getStatus(),
                productResponses
        );
    }
}
