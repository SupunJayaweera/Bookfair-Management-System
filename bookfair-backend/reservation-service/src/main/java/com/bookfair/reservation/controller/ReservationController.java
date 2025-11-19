package com.bookfair.reservation.controller;

import com.bookfair.reservation.dto.GenreRequest;
import com.bookfair.reservation.dto.ReservationRequest;
import com.bookfair.reservation.dto.ReservationResponse;
import com.bookfair.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestHeader("X-User-Id") String userEmail,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody ReservationRequest request) {
        try {
            // In a real scenario, you would extract userId from the user service
            // For now, we'll use a placeholder
            Long userId = 1L; // This should come from the authenticated user
            ReservationResponse response = reservationService.createReservation(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reservations/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @RequestHeader("X-User-Id") String userEmail) {
        try {
            Long userId = 1L; // Should be extracted from authenticated user
            List<ReservationResponse> reservations = reservationService.getUserReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        try {
            ReservationResponse reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reservations/{id}/genres")
    public ResponseEntity<Void> addGenres(
            @PathVariable Long id,
            @Valid @RequestBody GenreRequest request) {
        try {
            reservationService.addGenresToReservation(id, request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
