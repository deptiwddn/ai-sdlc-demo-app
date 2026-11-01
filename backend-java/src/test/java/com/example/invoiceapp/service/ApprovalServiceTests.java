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
    void directorApprovalRequiresSecondApprovalForAmountAboveLimit() {
        Invoice invoice = invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500001));
        ApprovalService service = serviceFor(invoice);

        Invoice result = service.approve("INV-1", "demo.director", "Director");

        assertEquals(InvoiceStatus.PendingSecondApproval, result.getStatus());
        assertEquals("demo.director", result.getFirstApprover());
        assertNull(result.getSecondApprover());
        assertEquals(InvoiceStatus.PendingSecondApproval, invoice.getStatus());
    }

    @Test
    void directorApprovalAllowsAmountAtLimit() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500000)));

        Invoice result = service.approve("INV-1", "demo.director", "Director");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.director", result.getDecidedBy());
    }

    @Test
    void directorApprovalAllowsAmountWithinLimit() {
        ApprovalService service = serviceFor(invoiceWith("INV-1", InvoiceStatus.PendingApproval, BigDecimal.valueOf(499999)));

        Invoice result = service.approve("INV-1", "demo.director", "Director");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.director", result.getDecidedBy());
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

    @Test
    void approveTransitionsLargeInvoiceToSecondApprovalWhenFirstApprovalIsRecorded() {
        Invoice invoice = invoiceWith("INV-100", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500001));
        ApprovalService service = serviceFor(invoice);

        Invoice result = service.approve("INV-100", "demo.director", "Director");

        assertEquals(InvoiceStatus.PendingSecondApproval, result.getStatus());
        assertEquals("demo.director", result.getFirstApprover());
        assertNull(result.getSecondApprover());
    }

    @Test
    void secondApprovalRequiresDifferentUser() {
        Invoice invoice = invoiceWith("INV-100", InvoiceStatus.PendingSecondApproval, BigDecimal.valueOf(500001));
        invoice.setFirstApprover("demo.director");
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-100", "demo.director", "Director"));

        assertEquals("The same user cannot approve the same high-value invoice twice.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingSecondApproval, invoice.getStatus());
    }

    @Test
    void secondApprovalRequiresDirectorRole() {
        Invoice invoice = invoiceWith("INV-100", InvoiceStatus.PendingSecondApproval, BigDecimal.valueOf(500001));
        invoice.setFirstApprover("demo.manager");
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-100", "demo.director", "Manager"));

        assertEquals("The second approver for an invoice over 500000 must be a Director.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingSecondApproval, invoice.getStatus());
    }

    @Test
    void secondDirectorApprovalCompletesHighValueApproval() {
        Invoice invoice = invoiceWith("INV-100", InvoiceStatus.PendingSecondApproval, BigDecimal.valueOf(500001));
        invoice.setFirstApprover("demo.manager");
        ApprovalService service = serviceFor(invoice);

        Invoice result = service.approve("INV-100", "demo.director", "Director");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertEquals("demo.director", result.getSecondApprover());
        assertEquals("demo.manager", invoice.getFirstApprover());
    }

    @Test
    void highValueInvoiceApprovesWithTwoDistinctDirectorApprovers() {
        Invoice invoice = invoiceWith("INV-200", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500001));
        ApprovalService service = serviceFor(invoice);

        Invoice firstApproval = service.approve("INV-200", "demo.director", "Director");

        assertEquals(InvoiceStatus.PendingSecondApproval, firstApproval.getStatus());
        assertEquals("demo.director", firstApproval.getFirstApprover());
        assertNull(firstApproval.getSecondApprover());

        Invoice secondApproval = service.approve("INV-200", "demo.admin", "Director");

        assertEquals(InvoiceStatus.Approved, secondApproval.getStatus());
        assertEquals("demo.director", secondApproval.getFirstApprover());
        assertEquals("demo.admin", secondApproval.getSecondApprover());
        assertEquals(InvoiceStatus.Approved, invoice.getStatus());
    }

    @Test
    void highValueInvoiceRequiresSecondApprovalBeforeCompletion() {
        Invoice invoice = invoiceWith("INV-201", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500001));
        ApprovalService service = serviceFor(invoice);

        Invoice approval = service.approve("INV-201", "demo.director", "Director");

        assertEquals(InvoiceStatus.PendingSecondApproval, approval.getStatus());
        assertEquals("demo.director", approval.getFirstApprover());
        assertNull(approval.getSecondApprover());
        assertEquals(InvoiceStatus.PendingSecondApproval, invoice.getStatus());
    }

    @Test
    void highValueInvoiceRejectsSameApproverUsedTwice() {
        Invoice invoice = invoiceWith("INV-202", InvoiceStatus.PendingSecondApproval, BigDecimal.valueOf(500001));
        invoice.setFirstApprover("demo.director");
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-202", "demo.director", "Director"));

        assertEquals("The same user cannot approve the same high-value invoice twice.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingSecondApproval, invoice.getStatus());
        assertEquals("demo.director", invoice.getFirstApprover());
        assertNull(invoice.getSecondApprover());
    }

    @Test
    void highValueInvoiceRejectsDuplicateApprovalBySameUser() {
        Invoice invoice = invoiceWith("INV-203", InvoiceStatus.Approved, BigDecimal.valueOf(500001));
        invoice.setDecidedBy("demo.admin");
        ApprovalService service = serviceFor(invoice);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.approve("INV-203", "demo.admin", "Director"));

        assertTrue(ex.getMessage().contains("only pending invoices can be decided"));
        assertEquals(InvoiceStatus.Approved, invoice.getStatus());
        assertEquals("demo.admin", invoice.getDecidedBy());
    }

    @Test
    void highValueInvoiceRejectsMissingSecondApproval() {
        Invoice invoice = invoiceWith("INV-204", InvoiceStatus.PendingSecondApproval, BigDecimal.valueOf(500001));
        invoice.setFirstApprover("demo.director");
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-204", "demo.manager", "Manager"));

        assertEquals("The second approver for an invoice over 500000 must be a Director.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingSecondApproval, invoice.getStatus());
        assertEquals("demo.director", invoice.getFirstApprover());
        assertNull(invoice.getSecondApprover());
    }

    @Test
    void highValueInvoiceRejectsInvalidApprovalSequence() {
        Invoice invoice = invoiceWith("INV-205", InvoiceStatus.Approved, BigDecimal.valueOf(500001));
        ApprovalService service = serviceFor(invoice);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.approve("INV-205", "demo.director", "Director"));

        assertTrue(ex.getMessage().contains("only pending invoices can be decided"));
        assertEquals(InvoiceStatus.Approved, invoice.getStatus());
        assertNull(invoice.getFirstApprover());
        assertNull(invoice.getSecondApprover());
    }

    @Test
    void highValueInvoiceRejectsApprovalAmountExceedingRoleAuthority() {
        Invoice invoice = invoiceWith("INV-206", InvoiceStatus.PendingApproval, BigDecimal.valueOf(500001));
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-206", "demo.manager", "Manager"));

        assertEquals("The first approver for an invoice over 500000 must be a Director.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingApproval, invoice.getStatus());
        assertNull(invoice.getFirstApprover());
        assertNull(invoice.getSecondApprover());
    }

    @Test
    void approveRejectsNullRole() {
        Invoice invoice = invoiceWith("INV-NULL-ROLE", InvoiceStatus.PendingApproval, BigDecimal.valueOf(1500));
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-NULL-ROLE", "demo.manager", null));

        assertEquals("A valid approval role is required.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingApproval, invoice.getStatus());
        assertNull(invoice.getDecidedBy());
    }

    @Test
    void rejectAllowsNullRoleWhenReasonIsProvided() {
        Invoice invoice = invoiceWith("INV-NULL-ROLE-REJECT", InvoiceStatus.PendingApproval, BigDecimal.valueOf(1500));
        ApprovalService service = serviceFor(invoice);

        Invoice result = service.reject("INV-NULL-ROLE-REJECT", "demo.approver", null, "Duplicate invoice");

        assertEquals(InvoiceStatus.Rejected, result.getStatus());
        assertEquals("Duplicate invoice", result.getDecisionNote());
    }

    @Test
    void approveAllowsNullApproverForRegularApproval() {
        ApprovalService service = serviceFor(invoiceWith("INV-NULL-APPROVER", InvoiceStatus.PendingApproval, BigDecimal.valueOf(1500)));

        Invoice result = service.approve("INV-NULL-APPROVER", null, "Manager");

        assertEquals(InvoiceStatus.Approved, result.getStatus());
        assertNull(result.getDecidedBy());
    }

    @Test
    void secondApprovalRejectsNullRoleBeforeApplyingDecisionLogic() {
        Invoice invoice = invoiceWith("INV-NULL-SECOND-ROLE", InvoiceStatus.PendingSecondApproval, BigDecimal.valueOf(500001));
        invoice.setFirstApprover("demo.director");
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.approve("INV-NULL-SECOND-ROLE", "demo.director", null));

        assertEquals("A valid approval role is required.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingSecondApproval, invoice.getStatus());
        assertEquals("demo.director", invoice.getFirstApprover());
    }

    @Test
    void rejectRequiresReasonEvenWhenRoleIsNull() {
        Invoice invoice = invoiceWith("INV-REJECT-NULL-ROLE", InvoiceStatus.PendingApproval, BigDecimal.valueOf(2500));
        ApprovalService service = serviceFor(invoice);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.reject("INV-REJECT-NULL-ROLE", "demo.approver", null, "   "));

        assertEquals("A reason is required to reject an invoice.", ex.getMessage());
        assertEquals(InvoiceStatus.PendingApproval, invoice.getStatus());
    }
}
