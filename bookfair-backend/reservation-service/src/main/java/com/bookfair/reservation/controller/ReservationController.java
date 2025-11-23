package com.bookfair.reservation.controller;

import com.bookfair.reservation.dto.GenreRequest;
import com.bookfair.reservation.dto.ReservationRequest;
import com.bookfair.reservation.dto.ReservationResponse;
import com.bookfair.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(
            @RequestHeader(value = "X-User-Id", required = false) String userEmail,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @Valid @RequestBody ReservationRequest request) {
        try {
            log.info("Received reservation request - User Email: {}, User Role: {}, Stalls: {}",
                    userEmail, userRole, request.getStallIds());

            if (userEmail == null || userEmail.isEmpty()) {
                log.error("Missing X-User-Id header");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User authentication required. Missing X-User-Id header."));
            }

            ReservationResponse response = reservationService.createReservation(userEmail, request);
            log.info("Successfully created reservation for user: {}", userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Failed to create reservation for user: {}", userEmail, e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reservations/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @RequestHeader("X-User-Id") String userEmail) {
        try {
            List<ReservationResponse> reservations = reservationService.getUserReservationsByEmail(userEmail);
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
