package br.edu.utfpr.apiDelivery.delivery.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "delivery_products")
public class DeliveryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(name = "product_id", nullable = false)
    private Long produtoId;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "product_quantity", nullable = false)
    private BigDecimal productQuantity;

    @Column(name = "product_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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