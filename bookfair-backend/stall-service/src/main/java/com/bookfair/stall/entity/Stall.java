package com.bookfair.stall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stalls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String stallName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StallSize size;

    @Column(nullable = false)
    private Double width;

    @Column(nullable = false)
    private Double length;

    @Column(nullable = false)
    private Double pricePerDay;

    @Column(nullable = false)
    private Integer positionX;

    @Column(nullable = false)
    private Integer positionY;

    @Column(nullable = false)
    private Boolean available = true;

    private String description;
}
