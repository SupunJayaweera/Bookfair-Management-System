package com.bookfair.notification.consumer;

import com.bookfair.notification.event.ReservationEvent;
import com.bookfair.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "reservation-notifications", 
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleReservationEvent(
            @Payload ReservationEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Received reservation event from partition: {}, offset: {} for reservation ID: {}", 
                partition, offset, event.getReservationId());

        try {
            notificationService.processReservationNotification(event);
            
            // Manual acknowledgment - only acknowledge if processing successful
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                log.debug("Message acknowledged for reservation ID: {}", event.getReservationId());
            }
            
        } catch (Exception e) {
            log.error("Failed to process reservation event for reservation ID: {} - Message will not be acknowledged", 
                    event.getReservationId(), e);
            // Don't acknowledge - message will be redelivered
            // In production, you might want to implement a dead letter queue after max retries
        }
    }
}
