package com.example.gstinvoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class InvoiceRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @Valid
    @NotEmpty(message = "At least one invoice item is required")
    private List<InvoiceItemDTO> items;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<InvoiceItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDTO> items) {
        this.items = items;
    }
}
