package com.example.invoiceapp.service;

import com.example.invoiceapp.model.Invoice;
import com.example.invoiceapp.model.InvoiceStatus;
import com.example.invoiceapp.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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

    public Invoice approve(String invoiceId, String approver) {
        Invoice invoice = find(invoiceId);
        ensurePending(invoice);

        invoice.setStatus(InvoiceStatus.Approved);
        invoice.setDecidedBy(approver);
        invoice.setDecidedAt(Instant.now());
        return repository.save(invoice);
    }

    public Invoice reject(String invoiceId, String approver, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A reason is required to reject an invoice.");
        }

        Invoice invoice = find(invoiceId);
        ensurePending(invoice);

        invoice.setStatus(InvoiceStatus.Rejected);
        invoice.setDecidedBy(approver);
        invoice.setDecidedAt(Instant.now());
        invoice.setDecisionNote(reason);
        return repository.save(invoice);
    }

    private Invoice find(String invoiceId) {
        return repository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice " + invoiceId + " was not found."));
    }

    private static void ensurePending(Invoice invoice) {
        if (invoice.getStatus() != InvoiceStatus.PendingApproval) {
            throw new IllegalStateException("Invoice " + invoice.getId() + " is " + invoice.getStatus() + "; only pending invoices can be decided.");
        }
    }
}

