package com.bookfair.stall.dto;

import com.bookfair.stall.entity.StallSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StallRequest {
    
    @NotBlank(message = "Stall name is required")
    private String stallName;
    
    @NotNull(message = "Size is required")
    private StallSize size;
    
    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    private Double width;
    
    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private Double length;
    
    @NotNull(message = "Price per day is required")
    @Positive(message = "Price must be positive")
    private Double pricePerDay;
    
    @NotNull(message = "Position X is required")
    private Integer positionX;
    
    @NotNull(message = "Position Y is required")
    private Integer positionY;
    
    private Boolean available = true;
    
    private String description;
}
