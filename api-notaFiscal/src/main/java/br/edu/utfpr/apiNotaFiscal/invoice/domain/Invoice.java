package br.edu.utfpr.apiNotaFiscal.invoice.domain;

import br.edu.utfpr.apiNotaFiscal.invoice.domain.enumeration.InvoiceStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String customer;

    private String customerCpf;

    private String customerEmail;

    private String customerCelphone;

    @Column(name = "customer_address_street", nullable = false)
    private String customerAddressStreet;

    @Column(name = "customer_address_number", nullable = false)
    private String customerAddressNumber;

    @Column(name = "customer_address_neighborhood", nullable = false)
    private String customerAddressNeighborhood;

    @Column(name = "customer_address_cep", nullable = false)
    private String customerAddressCep;

    @Column(name = "customer_address_city", nullable = false)
    private String customerAddressCity;

    @Column(name = "customer_address_state", nullable = false)
    private String customerAddressState;

    @Column(name = "customer_address_complement")
    private String customerAddressComplement;

    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "canceled_at")
    private LocalDate canceledAt;

    @Column(name = "status", nullable = false)
    private InvoiceStatus status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomerCpf() {
        return customerCpf;
    }

    public void setCustomerCpf(String customerCpf) {
        this.customerCpf = customerCpf;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerCelphone() {
        return customerCelphone;
    }

    public void setCustomerCelphone(String customerCelphone) {
        this.customerCelphone = customerCelphone;
    }

    public String getCustomerAddressStreet() {
        return customerAddressStreet;
    }

    public void setCustomerAddressStreet(String customerAddressStreet) {
        this.customerAddressStreet = customerAddressStreet;
    }

    public String getCustomerAddressNumber() {
        return customerAddressNumber;
    }

    public void setCustomerAddressNumber(String customerAddressNumber) {
        this.customerAddressNumber = customerAddressNumber;
    }

    public String getCustomerAddressNeighborhood() {
        return customerAddressNeighborhood;
    }

    public void setCustomerAddressNeighborhood(String customerAddressNeighborhood) {
        this.customerAddressNeighborhood = customerAddressNeighborhood;
    }

    public String getCustomerAddressCep() {
        return customerAddressCep;
    }

    public void setCustomerAddressCep(String customerAddressCep) {
        this.customerAddressCep = customerAddressCep;
    }

    public String getCustomerAddressCity() {
        return customerAddressCity;
    }

    public void setCustomerAddressCity(String customerAddressCity) {
        this.customerAddressCity = customerAddressCity;
    }

    public String getCustomerAddressState() {
        return customerAddressState;
    }

    public void setCustomerAddressState(String customerAddressState) {
        this.customerAddressState = customerAddressState;
    }

    public String getCustomerAddressComplement() {
        return customerAddressComplement;
    }

    public void setCustomerAddressComplement(String customerAddressComplement) {
        this.customerAddressComplement = customerAddressComplement;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDate canceledAt) {
        this.canceledAt = canceledAt;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
}
