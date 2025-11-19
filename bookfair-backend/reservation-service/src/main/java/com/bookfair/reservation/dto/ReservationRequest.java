package com.bookfair.reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotEmpty(message = "At least one stall must be selected")
    @Size(max = 3, message = "Maximum 3 stalls can be reserved per business")
    private Set<Long> stallIds;
}
