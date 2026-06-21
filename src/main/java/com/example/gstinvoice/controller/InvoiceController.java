package com.example.gstinvoice.controller;

import com.example.gstinvoice.dto.InvoiceRequest;
import com.example.gstinvoice.entity.Invoice;
import com.example.gstinvoice.entity.InvoiceStatus;
import com.example.gstinvoice.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        Invoice invoice = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Invoice>> getInvoicesByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomerId(customerId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Invoice> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status
    ) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, BigDecimal>> getTotalRevenue() {
        return ResponseEntity.ok(Map.of("totalRevenue", invoiceService.getTotalRevenue()));
    }
}
