package com.example.invoiceapp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private java.math.BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.Draft;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private String decidedBy;
    private Instant decidedAt;
    private String decisionNote;

    public Invoice() {
    }

    public Invoice(String customerName, java.math.BigDecimal amount) {
        this.customerName = customerName;
        this.amount = amount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public java.math.BigDecimal getAmount() { return amount; }
    public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getDecidedBy() { return decidedBy; }
    public void setDecidedBy(String decidedBy) { this.decidedBy = decidedBy; }
    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }
    public String getDecisionNote() { return decisionNote; }
    public void setDecisionNote(String decisionNote) { this.decisionNote = decisionNote; }
}

