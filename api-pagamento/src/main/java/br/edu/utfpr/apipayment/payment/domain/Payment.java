package br.edu.utfpr.apipayment.payment.domain;

import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentStatus;
import br.edu.utfpr.apipayment.payment.domain.enumeration.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, updatable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @Column(name = "buyer_cpf", nullable = false)
    private String buyerCpf;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerCpf() {
        return buyerCpf;
    }

    public void setBuyerCpf(String buyerCpf) {
        this.buyerCpf = buyerCpf;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate paymentDate) {
        this.createdAt = paymentDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }
}
