package com.agribridge.farm.dto;

import com.agribridge.farm.Farm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmResponse {
    private Long id;
    private Long ownerId;
    private String farmName;
    private String location;
    private Double landArea;
    private String soilType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FarmResponse fromEntity(Farm farm) {
        return FarmResponse.builder()
                .id(farm.getId())
                .ownerId(farm.getOwnerId())
                .farmName(farm.getFarmName())
                .location(farm.getLocation())
                .landArea(farm.getLandArea())
                .soilType(farm.getSoilType())
                .createdAt(farm.getCreatedAt())
                .updatedAt(farm.getUpdatedAt())
                .build();
    }
}
