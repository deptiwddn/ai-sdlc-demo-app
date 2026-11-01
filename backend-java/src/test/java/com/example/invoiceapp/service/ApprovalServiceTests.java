package com.example.invoiceapp.service;

import com.example.invoiceapp.model.Invoice;
import com.example.invoiceapp.model.InvoiceStatus;
import com.example.invoiceapp.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApprovalServiceTests {
    private static Invoice invoiceWith(String id, InvoiceStatus status, BigDecimal amount) {
        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setCustomerName("Test School");
        invoice.setAmount(amount);
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
        Invoice invoice = invoiceWith("INV-1", InvoiceStatus.Draft, BigDecimal.valueOf(100));

        Invoice result = serviceFor(invoice).submit("INV-1");

        assertEquals(InvoiceStatus.PendingApproval, result.getStatus());
    }

    @Test
    void submitRejectsInvoiceThatIsNotADraft() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.Approved, BigDecimal.valueOf(100)));

        assertThrows(IllegalStateException.class, () -> service.submit("INV-1"));
    }

    @Test
    void approveSetsStatusApproverAndTimestamp() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(100)));

        Invoice result = service.approve("INV-1", "demo.manager", "Manager");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.manager", result.getDecidedBy());
        assertNotNull(result.getDecidedAt());
    }

    @Test
    void managerApprovalRejectsInvoiceAboveLimit() {
        Invoice invoice = invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(50001));
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-1", "demo.manager", "Manager"));

        assertTrue(ex.getMessage().contains("Manager approval limit exceeded"));
        assertEquals(InvoiceStatus.PendingApproval, invoice.getStatus());
    }

    @Test
    void managerApprovalAllowsAmountWithinLimit() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(49999)));

        Invoice result = service.approve("INV-1", "demo.manager", "Manager");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.manager", result.getDecidedBy());
    }

    @Test
    void managerApprovalAllowsAmountAtLimit() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(50000)));

        Invoice result = service.approve("INV-1", "demo.manager", "Manager");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.manager", result.getDecidedBy());
    }

    @Test
    void managerApprovalRejectsAmountAboveLimitWithoutMutatingState() {
        Invoice invoice = invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(50001));
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-1", "demo.manager", "Manager"));

        assertEquals("Manager approval limit exceeded: invoice amount 50001 exceeds the maximum of 50000.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingApproval, invoice.getStatus());
        assertNull(invoice.getDecidedBy());
        assertNull(invoice.getDecidedAt());
    }

    @Test
    void directorApprovalRejectsInvoiceAboveLimit() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500001)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-1", "demo.director", "Director"));

        assertTrue(ex.getMessage().contains("Director approval limit exceeded"));
    }

    @Test
    void directorApprovalAllowsAmountWithinLimit() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(499999)));

        Invoice result = service.approve("INV-1", "demo.director", "Director");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.director", result.getDecidedBy());
    }

    @Test
    void directorApprovalAllowsAmountAtLimit() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500000)));

        Invoice result = service.approve("INV-1", "demo.director", "Director");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.director", result.getDecidedBy());
    }

    @Test
    void directorApprovalRejectsAmountAboveLimitWithoutMutatingState() {
        Invoice invoice = invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500001));
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-1", "demo.director", "Director"));

        assertEquals("Director approval limit exceeded: invoice amount 500001 exceeds the maximum of 500000.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingApproval, invoice.getStatus());
        assertNull(invoice.getDecidedBy());
        assertNull(invoice.getDecidedAt());
    }

    @Test
    void approveRejectsInvoiceThatIsStillADraft() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.Draft, BigDecimal.valueOf(100)));

        assertThrows(IllegalStateException.class, () -> service.approve("INV-1", "demo.approver", "Approver"));
    }

    @Test
    void approveThrowsWhenInvoiceDoesNotExist() {
        ApprovalService service = serviceFor();

        assertThrows(IllegalArgumentException.class, () -> service.approve("INV-404", "demo.approver", "Approver"));
    }

    @Test
    void rejectRecordsReasonAndApprover() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(100)));

        Invoice result = service.reject("INV-1", "demo.approver", "Duplicate invoice");

        assertEquals(InvoiceStatus.Rejected, result.getStatus());
        assertEquals("Duplicate invoice", result.getDecisionNote());
    }

    @Test
    void rejectRequiresAReason() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(100)));

        assertThrows(IllegalArgumentException.class, () -> service.reject("INV-1", "demo.approver", "  "));
    }
}
