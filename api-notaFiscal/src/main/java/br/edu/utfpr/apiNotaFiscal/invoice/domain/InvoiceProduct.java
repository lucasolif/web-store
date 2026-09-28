package br.edu.utfpr.apiNotaFiscal.invoice.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_products")
public class InvoiceProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "product_quantity", nullable = false)
    private BigDecimal productQuantity;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(BigDecimal productQuantity) {
        this.productQuantity = productQuantity;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
