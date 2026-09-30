package com.agribridge.crop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * WEEK 3 — Crop Management is now implemented (see CropService,
 * CropController). This entity and its matching `crops` table
 * (database/schema.sql) were introduced in Week 2 as part of the full
 * planned data model; Week 3 adds the working service/controller layer on
 * top of it.
 */
@Entity
@Table(name = "crops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "farm_id", nullable = false)
    private Long farmId;

    @Column(name = "crop_name", nullable = false, length = 100)
    private String cropName;

    @Column(name = "crop_type", length = 100)
    private String cropType;

    @Column(name = "sowing_date")
    private LocalDate sowingDate;

    @Column(name = "expected_harvest_date")
    private LocalDate expectedHarvestDate;

    @Column(length = 30)
    private String status; // e.g., PLANNED, GROWING, HARVEST_READY, COMPLETED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
