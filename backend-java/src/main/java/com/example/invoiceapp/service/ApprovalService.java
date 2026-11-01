package com.example.invoiceapp.service;

import com.example.invoiceapp.auth.Role;
import com.example.invoiceapp.model.Invoice;
import com.example.invoiceapp.model.InvoiceStatus;
import com.example.invoiceapp.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

@Service
public class ApprovalService {
    private final InvoiceRepository repository;

    public ApprovalService(InvoiceRepository repository) {
        this.repository = repository;
    }

    public Invoice submit(String invoiceId) {
        Invoice invoice = find(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.Draft) {
            throw new IllegalStateException("Invoice " + invoiceId + " is " + invoice.getStatus() + "; only drafts can be submitted.");
        }

        invoice.setStatus(InvoiceStatus.PendingApproval);
        return repository.save(invoice);
    }

    public Invoice approve(String invoiceId, String approver, String role) {
        validateApprovalRole(role);
        Invoice invoice = find(invoiceId);
        ensurePending(invoice);

        if (requiresSecondApproval(invoice)) {
            return approveHighValueInvoice(invoice, approver, role);
        }

        validateApprovalLimit(invoice, role);

        invoice.setStatus(InvoiceStatus.Approved);
        invoice.setDecidedBy(approver);
        invoice.setDecidedAt(Instant.now());
        invoice.setDecisionNote("Approved by " + approver);
        return repository.save(invoice);
    }

    public Invoice reject(String invoiceId, String approver, String reason) {
        return reject(invoiceId, approver, null, reason);
    }

    public Invoice reject(String invoiceId, String approver, String role, String reason) {
        if (role != null) {
            validateApprovalRole(role);
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A reason is required to reject an invoice.");
        }

        Invoice invoice = find(invoiceId);
        ensurePendingOrSecondApproval(invoice);

        invoice.setStatus(InvoiceStatus.Rejected);
        invoice.setDecidedBy(approver);
        invoice.setDecidedAt(Instant.now());
        invoice.setDecisionNote(reason);
        return repository.save(invoice);
    }

    private Invoice approveHighValueInvoice(Invoice invoice, String approver, String role) {
        if (invoice.getStatus() == InvoiceStatus.PendingApproval) {
            if (Role.DIRECTOR.value().equals(role)) {
                invoice.setFirstApprover(approver);
                invoice.setApprovalReason("First approval recorded.");
                invoice.setStatus(InvoiceStatus.PendingSecondApproval);
                return repository.save(invoice);
            }
            throw new IllegalArgumentException("The first approver for an invoice over 500000 must be a Director.");
        }

        if (invoice.getStatus() == InvoiceStatus.PendingSecondApproval) {
            if (invoice.getFirstApprover() != null && invoice.getFirstApprover().equals(approver)) {
                throw new IllegalArgumentException("The same user cannot approve the same high-value invoice twice.");
            }

            if (!Role.DIRECTOR.value().equals(role)) {
                throw new IllegalArgumentException("The second approver for an invoice over 500000 must be a Director.");
            }

            invoice.setSecondApprover(approver);
            invoice.setStatus(InvoiceStatus.Approved);
            invoice.setDecidedBy(approver);
            invoice.setDecidedAt(Instant.now());
            invoice.setDecisionNote("Approved by second director approver: " + approver);
            return repository.save(invoice);
        }

        throw new IllegalStateException("Invoice " + invoice.getId() + " is " + invoice.getStatus() + "; only pending invoices can be decided.");
    }

    private Invoice find(String invoiceId) {
        return repository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice " + invoiceId + " was not found."));
    }

    private boolean requiresSecondApproval(Invoice invoice) {
        return invoice.getAmount().compareTo(BigDecimal.valueOf(500000)) > 0;
    }

    private void validateApprovalLimit(Invoice invoice, String role) {
        if (role == null || role.isBlank()) {
            return;
        }

        BigDecimal amount = invoice.getAmount();
        if (Role.MANAGER.value().equals(role) && amount.compareTo(BigDecimal.valueOf(50000)) > 0) {
            throw new IllegalArgumentException("Manager approval limit exceeded: invoice amount " + amount + " exceeds the maximum of 50000.");
        }

        if (Role.DIRECTOR.value().equals(role) && amount.compareTo(BigDecimal.valueOf(500000)) > 0) {
            throw new IllegalArgumentException("Director approval limit exceeded: invoice amount " + amount + " exceeds the maximum of 500000.");
        }
    }

    private static void ensurePending(Invoice invoice) {
        if (invoice.getStatus() != InvoiceStatus.PendingApproval && invoice.getStatus() != InvoiceStatus.PendingSecondApproval) {
            throw new IllegalStateException("Invoice " + invoice.getId() + " is " + invoice.getStatus() + "; only pending invoices can be decided.");
        }
    }

    private static void ensurePendingOrSecondApproval(Invoice invoice) {
        if (invoice.getStatus() != InvoiceStatus.PendingApproval && invoice.getStatus() != InvoiceStatus.PendingSecondApproval) {
            throw new IllegalStateException("Invoice " + invoice.getId() + " is " + invoice.getStatus() + "; only pending invoices can be rejected.");
        }
    }

    private void validateApprovalRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("A valid approval role is required.");
        }

        boolean validRole = Arrays.stream(Role.values())
                .map(Role::value)
                .anyMatch(role::equals);

        if (!validRole) {
            throw new IllegalArgumentException("Unsupported approval role: " + role + ".");
        }
    }
}
