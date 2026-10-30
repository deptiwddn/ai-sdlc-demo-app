package com.example.invoiceapp.controller;

import com.example.invoiceapp.model.Invoice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public abstract class ApiControllerBase {
    protected ResponseEntity<?> handle(java.util.function.Supplier<Invoice> action) {
        try {
            return ResponseEntity.ok(action.get());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
        }
    }
}

