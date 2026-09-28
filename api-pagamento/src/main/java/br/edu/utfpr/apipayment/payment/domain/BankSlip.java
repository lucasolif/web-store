package br.edu.utfpr.apipayment.payment.domain;

import br.edu.utfpr.apipayment.payment.domain.enumeration.BankSlipStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_slips")
public class BankSlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "barcode", nullable = false, unique = true, length = 44)
    private String barcode;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payer_name", nullable = false)
    private String payerName;

    @Column(name = "payer_cpf", nullable = false)
    private String payerCpf;

    @Column(name = "generated_at", nullable = false)
    private LocalDate generatedAt;

    @Column(name = "canceled_at")
    private LocalDate canceledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BankSlipStatus status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerCpf() {
        return payerCpf;
    }

    public void setPayerCpf(String payerCpf) {
        this.payerCpf = payerCpf;
    }

    public LocalDate getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDate generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDate getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDate canceledAt) {
        this.canceledAt = canceledAt;
    }

    public BankSlipStatus getStatus() {
        return status;
    }

    public void setStatus(BankSlipStatus status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
