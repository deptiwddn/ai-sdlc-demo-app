package com.example.invoiceapp.service;

import com.example.invoiceapp.model.Invoice;
import com.example.invoiceapp.model.InvoiceStatus;
import com.example.invoiceapp.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApprovalServiceTests {
    private static Invoice invoiceWith(String id, InvoiceStatus status) {
        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setCustomerName("Test School");
        invoice.setAmount(java.math.BigDecimal.valueOf(100));
        invoice.setStatus(status);
        return invoice;
    }

    private static ApprovalService serviceFor(Invoice... invoices) {
        Map<String, Invoice> byId = new HashMap<>();
        for (Invoice invoice : invoices) {
            byId.put(invoice.getId(), invoice);
        }

        InvoiceRepository repository = mock(InvoiceRepository.class);
        when(repository.findById(anyString())).thenAnswer(invocation -> Optional.ofNullable(byId.get(invocation.getArgument(0))));
        when(repository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            byId.put(invoice.getId(), invoice);
            return invoice;
        });

        return new ApprovalService(repository);
    }

    @Test
    void submitMovesDraftToPendingApproval() {
        Invoice invoice = invoiceWith("INV-1", InvoiceStatus.Draft);

        Invoice result = serviceFor(invoice).submit("INV-1");

        assertEquals(InvoiceStatus.PendingApproval, result.getStatus());
    }

    @Test
    void submitRejectsInvoiceThatIsNotADraft() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.Approved));

        assertThrows(IllegalStateException.class, () -> service.submit("INV-1"));
    }

    @Test
    void approveSetsStatusApproverAndTimestamp() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval));

        Invoice result = service.approve("INV-1", "demo.approver");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.approver", result.getDecidedBy());
        assertNotNull(result.getDecidedAt());
    }

    @Test
    void approveRejectsInvoiceThatIsStillADraft() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.Draft));

        assertThrows(IllegalStateException.class, () -> service.approve("INV-1", "demo.approver"));
    }

    @Test
    void approveThrowsWhenInvoiceDoesNotExist() {
        ApprovalService service = serviceFor();

        assertThrows(IllegalArgumentException.class, () -> service.approve("INV-404", "demo.approver"));
    }

    @Test
    void rejectRecordsReasonAndApprover() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval));

        Invoice result = service.reject("INV-1", "demo.approver", "Duplicate invoice");

        assertEquals(InvoiceStatus.Rejected, result.getStatus());
        assertEquals("Duplicate invoice", result.getDecisionNote());
    }

    @Test
    void rejectRequiresAReason() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval));

        assertThrows(IllegalArgumentException.class, () -> service.reject("INV-1", "demo.approver", "  "));
    }
}
