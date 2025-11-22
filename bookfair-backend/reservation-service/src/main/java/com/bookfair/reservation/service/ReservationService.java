package com.bookfair.reservation.service;

import com.bookfair.reservation.client.StallClient;
import com.bookfair.reservation.client.UserClient;
import com.bookfair.reservation.dto.*;
import com.bookfair.reservation.entity.Reservation;
import com.bookfair.reservation.event.ReservationEvent;
import com.bookfair.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final QRCodeService qrCodeService;
    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;
    private final StallClient stallClient;
    private final UserClient userClient;

    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationRequest request) {
        // Check if user already has reservations
        List<Reservation> existingReservations = reservationRepository.findByUserId(userId);
        long totalStalls = existingReservations.stream()
                .flatMap(r -> r.getStallIds().stream())
                .count();

        if (totalStalls + request.getStallIds().size() > 3) {
            throw new RuntimeException("Maximum 3 stalls can be reserved per business");
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setStallIds(request.getStallIds());
        reservation.setQrCode(qrCodeService.generateQRCode());

        Reservation savedReservation = reservationRepository.save(reservation);

        // Update stall availability
        request.getStallIds().forEach(stallId -> {
            try {
                stallClient.updateStallAvailability(stallId, false);
            } catch (Exception e) {
                log.error("Failed to update stall availability for stall {}", stallId, e);
            }
        });

        // Get user details and send notification via Kafka
        try {
            UserResponse user = userClient.getUserById(userId);
            ReservationEvent event = new ReservationEvent(
                    savedReservation.getId(),
                    userId,
                    user.getEmail(),
                    savedReservation.getStallIds(),
                    savedReservation.getQrCode());
            kafkaTemplate.send("reservation-notifications", event);
            log.info("Sent reservation notification event for reservation {}", savedReservation.getId());
        } catch (Exception e) {
            log.error("Failed to send reservation notification", e);
        }

        return mapToReservationResponse(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return mapToReservationResponse(reservation);
    }

    @Transactional
    public void addGenresToReservation(Long reservationId, GenreRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.getLiteraryGenres().addAll(request.getGenres());
        reservationRepository.save(reservation);
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setUserId(reservation.getUserId());
        response.setStallIds(reservation.getStallIds());
        response.setQrCode(reservation.getQrCode());
        response.setStatus(reservation.getStatus());
        response.setReservedAt(reservation.getReservedAt());
        response.setLiteraryGenres(reservation.getLiteraryGenres());
        return response;
    }
}
