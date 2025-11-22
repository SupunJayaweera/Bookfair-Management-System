package com.bookfair.reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreRequest {

    @NotEmpty(message = "At least one genre must be selected")
    private Set<String> genres;
}
