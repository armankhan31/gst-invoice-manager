package com.example.gstinvoice.service;

import com.example.gstinvoice.dto.InvoiceItemDTO;
import com.example.gstinvoice.dto.InvoiceRequest;
import com.example.gstinvoice.entity.Customer;
import com.example.gstinvoice.entity.Invoice;
import com.example.gstinvoice.entity.InvoiceItem;
import com.example.gstinvoice.entity.InvoiceStatus;
import com.example.gstinvoice.exception.ResourceNotFoundException;
import com.example.gstinvoice.repository.CustomerRepository;
import com.example.gstinvoice.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.09");

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Invoice createInvoice(InvoiceRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setInvoiceDate(LocalDate.now());

        invoice.setStatus(InvoiceStatus.UNPAID);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (InvoiceItemDTO itemDTO : request.getItems()) {
            BigDecimal totalPrice = itemDTO.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            InvoiceItem item = new InvoiceItem();
            item.setItemName(itemDTO.getItemName());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
            item.setTotalPrice(totalPrice);

            invoice.addItem(item);
            subtotal = subtotal.add(totalPrice);
        }

        BigDecimal cgst = subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sgst = subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = subtotal.add(cgst).add(sgst).setScale(2, RoundingMode.HALF_UP);

        invoice.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        invoice.setCgst(cgst);
        invoice.setSgst(sgst);
        invoice.setGrandTotal(grandTotal);

        // ✅ Save first so DB assigns the ID
        Invoice saved = invoiceRepository.save(invoice);

        // ✅ Now use that ID to generate a guaranteed unique invoice number
        saved.setInvoiceNumber(String.format("INV-%03d", saved.getId()));

        return invoiceRepository.save(saved); // save again with the number set
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    public List<Invoice> getInvoicesByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return invoiceRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Invoice updateInvoiceStatus(Long id, InvoiceStatus newStatus) {
        Invoice invoice = getInvoiceById(id);
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update status of a cancelled invoice.");
        }
        invoice.setStatus(newStatus);
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = getInvoiceById(id);
        invoiceRepository.delete(invoice);
    }

    public BigDecimal getTotalRevenue() {
        return invoiceRepository.calculateTotalRevenue().setScale(2, RoundingMode.HALF_UP);
    }

}
