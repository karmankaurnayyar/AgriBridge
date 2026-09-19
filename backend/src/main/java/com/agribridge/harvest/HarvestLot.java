package com.agribridge.harvest;

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
 * PLANNED FOR WEEK 3 — see docs/roadmap.md.
 *
 * Schema-ready entity (see database/schema.sql, table `harvest_lots`);
 * HarvestLotRepository has no accompanying service or controller yet.
 */
@Entity
@Table(name = "harvest_lots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lot_code", unique = true, length = 40)
    private String lotCode;

    @Column(name = "crop_id", nullable = false)
    private Long cropId;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 20)
    private String unit; // e.g., kg, quintal

    @Column(name = "harvest_date")
    private LocalDate harvestDate;

    @Column(name = "quality_grade", length = 20)
    private String qualityGrade;

    @Column(name = "expected_price", precision = 10, scale = 2)
    private BigDecimal expectedPrice;

    @Column(name = "availability_status", length = 20)
    private String availabilityStatus; // e.g., AVAILABLE, RESERVED, SOLD

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
