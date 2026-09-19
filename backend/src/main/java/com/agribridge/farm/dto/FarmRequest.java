package com.agribridge.farm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmRequest {

    @NotBlank(message = "Farm name is required")
    @Size(max = 150)
    private String farmName;

    @NotBlank(message = "Location is required")
    @Size(max = 200)
    private String location;

    @NotNull(message = "Land area is required")
    @DecimalMin(value = "0.01", message = "Land area must be greater than zero")
    private Double landArea;

    @Size(max = 100)
    private String soilType;
}
