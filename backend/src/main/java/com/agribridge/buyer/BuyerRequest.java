package com.agribridge.buyer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PLANNED FOR WEEK 4 (buyer requests + rule-based matching) — see
 * docs/roadmap.md.
 *
 * Schema-ready entity (see database/schema.sql, table `buyer_requests`);
 * BuyerRequestRepository has no accompanying service or controller yet,
 * and the `matches` table exists in the schema but has no entity yet -
 * both are planned for Week 4 alongside the matching engine.
 */
@Entity
@Table(name = "buyer_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(nullable = false, length = 100)
    private String commodity;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 10, scale = 2)
    private BigDecimal maxPrice;

    @Column(length = 200)
    private String location;

    @Column(name = "required_by_date")
    private LocalDate requiredByDate;

    @Column(length = 20)
    private String status; // e.g., OPEN, MATCHED, CANCELLED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
