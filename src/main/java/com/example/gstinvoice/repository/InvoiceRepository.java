package com.example.gstinvoice.repository;

import com.example.gstinvoice.entity.Invoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "items"})
    List<Invoice> findAll();

    @Override
    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<Invoice> findById(Long id);

    @EntityGraph(attributePaths = {"customer", "items"})
    List<Invoice> findByCustomerId(Long customerId);

    Optional<Invoice> findTopByOrderByIdDesc();

    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal calculateTotalRevenue();
}
