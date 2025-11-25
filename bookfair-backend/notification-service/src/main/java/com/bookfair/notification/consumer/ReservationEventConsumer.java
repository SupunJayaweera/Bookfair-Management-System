package com.bookfair.notification.consumer;

import com.bookfair.notification.event.ReservationEvent;
import com.bookfair.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "reservation-notifications", groupId = "notification-service-group")
    public void handleReservationEvent(ReservationEvent event) {
        log.info("Received reservation event for reservation ID: {}", event.getReservationId());

        try {
            emailService.sendReservationConfirmation(
                    event.getUserEmail(),
                    event.getReservationId(),
                    event.getStallIds(),
                    event.getQrCode());
            log.info("Successfully processed reservation event for reservation ID: {}", event.getReservationId());
        } catch (Exception e) {
            log.error("Failed to process reservation event for reservation ID: {}", event.getReservationId(), e);
        }
    }
}
