package com.example.invoiceapp.controller;

import com.example.invoiceapp.auth.RequireRole;
import com.example.invoiceapp.auth.UserSession;
import com.example.invoiceapp.model.Invoice;
import com.example.invoiceapp.service.ApprovalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approvals")
@RequireRole({"Approver", "Admin"})
public class ApprovalsController extends ApiControllerBase {
    private final ApprovalService approvals;

    public ApprovalsController(ApprovalService approvals) {
        this.approvals = approvals;
    }

    @PostMapping("/{invoiceId}/approve")
    public ResponseEntity<?> approve(@PathVariable String invoiceId, HttpServletRequest request) {
        UserSession session = (UserSession) request.getAttribute("session");
        return handle(() -> approvals.approve(invoiceId, session.username()));
    }

    @PostMapping("/{invoiceId}/reject")
    public ResponseEntity<?> reject(@PathVariable String invoiceId, @RequestBody RejectRequest requestBody, HttpServletRequest request) {
        UserSession session = (UserSession) request.getAttribute("session");
        return handle(() -> approvals.reject(invoiceId, session.username(), requestBody.reason()));
    }

    public record RejectRequest(String reason) {
    }
}

