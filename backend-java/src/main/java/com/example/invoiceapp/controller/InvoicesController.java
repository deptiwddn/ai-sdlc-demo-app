package com.example.invoiceapp.controller;

import com.example.invoiceapp.auth.RequireRole;
import com.example.invoiceapp.model.Invoice;
import com.example.invoiceapp.model.InvoiceStatus;
import com.example.invoiceapp.repository.InvoiceRepository;
import com.example.invoiceapp.service.ApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
public class InvoicesController extends ApiControllerBase {
    private final InvoiceRepository repository;
    private final ApprovalService approvals;

    public InvoicesController(InvoiceRepository repository, ApprovalService approvals) {
        this.repository = repository;
        this.approvals = approvals;
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAll(@RequestParam(required = false) String status) {
        List<Invoice> invoices = repository.findAll();
        if (status != null && !status.isBlank()) {
            try {
                InvoiceStatus parsed = InvoiceStatus.valueOf(status.trim());
                invoices = invoices.stream().filter(i -> i.getStatus() == parsed).toList();
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateInvoiceRequest request) {
        if (request.customerName() == null || request.customerName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer name is required."));
        }
        if (request.amount() == null || request.amount().doubleValue() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Amount must be greater than zero."));
        }

        Invoice invoice = new Invoice(request.customerName().trim(), request.amount());
        if (invoice.getId() == null || invoice.getId().isBlank()) {
            invoice.setId("INV-" + System.currentTimeMillis());
        }
        Invoice created = repository.save(invoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submit(@PathVariable String id) {
        return handle(() -> approvals.submit(id));
    }

    @DeleteMapping("/{id}")
    @RequireRole({"Admin"})
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public record CreateInvoiceRequest(String customerName, java.math.BigDecimal amount) {
    }
}

