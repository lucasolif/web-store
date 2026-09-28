package br.edu.utfpr.apiDelivery.delivery.domain;

import br.edu.utfpr.apiDelivery.delivery.domain.enumeration.DeliveryStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customer;

    @Column(name = "customer_cpf", nullable = false, length = 11)
    private String customerCpf;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_celphone", nullable = false)
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

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "delivery_created_at", nullable = false)
    private LocalDate deliveryCreatedAt;

    @Column(name = "delivery_prevision")
    private LocalDate deliveryPrevision;

    @Column(name = "tracking_code", unique = true, length = 10)
    private String trackingCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryProduct> products = new ArrayList<>();


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

    public LocalDate getDeliveryCreatedAt() {
        return deliveryCreatedAt;
    }

    public void setDeliveryCreatedAt(LocalDate deliveryCreatedAt) {
        this.deliveryCreatedAt = deliveryCreatedAt;
    }

    public LocalDate getDeliveryPrevision() {
        return deliveryPrevision;
    }

    public void setDeliveryPrevision(LocalDate deliveryPrevision) {
        this.deliveryPrevision = deliveryPrevision;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public List<DeliveryProduct> getProducts() {
        return products;
    }

    public void setProducts(List<DeliveryProduct> products) {
        this.products = products;
    }
}