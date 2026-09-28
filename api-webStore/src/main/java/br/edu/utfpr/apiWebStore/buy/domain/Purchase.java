package br.edu.utfpr.apiWebStore.buy.domain;

import br.edu.utfpr.apiWebStore.buy.domain.enumeration.PurchaseStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "buyer_total_value", nullable = false)
    private BigDecimal buyTotalValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PurchaseStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getBuyTotalValue() {
        return buyTotalValue;
    }

    public void setBuyTotalValue(BigDecimal buyerTotalValue) {
        this.buyTotalValue = buyerTotalValue;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }
}
