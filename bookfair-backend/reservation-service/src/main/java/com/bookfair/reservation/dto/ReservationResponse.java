package com.bookfair.reservation.dto;

import com.bookfair.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private Long userId;
    private Set<Long> stallIds;
    private String qrCode;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
    private Set<String> literaryGenres;
}
