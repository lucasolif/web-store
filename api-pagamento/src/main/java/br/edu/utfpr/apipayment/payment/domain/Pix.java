package br.edu.utfpr.apipayment.payment.domain;

import br.edu.utfpr.apipayment.payment.domain.enumeration.PixStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "pix")
public class Pix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "qr_code", nullable = false, unique = true, length = 64)
    private String qrCode;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "expiration_time", nullable = false)
    private LocalTime expirationTime;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payer_name", nullable = false)
    private String payerName;

    @Column(name = "payer_cpf", nullable = false)
    private String payerCpf;

    @Column(name = "generation_date", nullable = false)
    private LocalDate generationDate;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PixStatus status;


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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalTime expirationTime) {
        this.expirationTime = expirationTime;
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

    public LocalDate getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(LocalDate generationDate) {
        this.generationDate = generationDate;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public PixStatus getStatus() {
        return status;
    }

    public void setStatus(PixStatus status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
