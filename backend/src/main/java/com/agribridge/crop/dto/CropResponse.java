package com.agribridge.crop.dto;

import com.agribridge.crop.Crop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropResponse {
    private Long id;
    private Long farmId;
    private String cropName;
    private String cropType;
    private LocalDate sowingDate;
    private LocalDate expectedHarvestDate;
    private String status;
    private LocalDateTime createdAt;

    public static CropResponse fromEntity(Crop crop) {
        return CropResponse.builder()
                .id(crop.getId())
                .farmId(crop.getFarmId())
                .cropName(crop.getCropName())
                .cropType(crop.getCropType())
                .sowingDate(crop.getSowingDate())
                .expectedHarvestDate(crop.getExpectedHarvestDate())
                .status(crop.getStatus())
                .createdAt(crop.getCreatedAt())
                .build();
    }
}
