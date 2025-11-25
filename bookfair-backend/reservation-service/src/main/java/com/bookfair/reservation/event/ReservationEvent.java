package com.bookfair.reservation.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEvent {
    private Long reservationId;
    private Long userId;
    private String userEmail;
    private Set<Long> stallIds;
    private String qrCode;
}
