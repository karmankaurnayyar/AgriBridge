package com.agribridge.crop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Week 3 — Crop Management request payload.
 *
 * farmId is included in the body (rather than the URL path) to match the
 * flat /api/crops contract already documented in Week 2
 * (docs/api-documentation.md, "Planned Endpoints").
 */
@Getter
@Setter
public class CropRequest {

    @NotNull(message = "farmId is required")
    private Long farmId;

    @NotBlank(message = "Crop name is required")
    @Size(max = 100)
    private String cropName;

    @Size(max = 100)
    private String cropType;

    private LocalDate sowingDate;

    private LocalDate expectedHarvestDate;

    /** One of PLANNED, GROWING, HARVEST_READY, COMPLETED. Defaults to PLANNED if omitted. */
    private String status;
}
