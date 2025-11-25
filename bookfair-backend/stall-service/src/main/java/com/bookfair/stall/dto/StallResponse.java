package com.bookfair.stall.dto;

import com.bookfair.stall.entity.StallSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StallResponse {
    private Long id;
    private String stallName;
    private StallSize size;
    private Double width;
    private Double length;
    private Double pricePerDay;
    private Integer positionX;
    private Integer positionY;
    private Boolean available;
    private String description;
}
