package com.bookfair.notification.service;

import com.bookfair.notification.entity.NotificationHistory;
import com.bookfair.notification.entity.NotificationStatus;
import com.bookfair.notification.entity.NotificationType;
import com.bookfair.notification.event.ReservationEvent;
import com.bookfair.notification.exception.NotificationProcessingException;
import com.bookfair.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    
    @Transactional
    public void processReservationNotification(ReservationEvent event) {
        log.info("Processing reservation notification for reservation ID: {}", event.getReservationId());
        
        // Create notification history record
        NotificationHistory notification = NotificationHistory.builder()
                .reservationId(event.getReservationId())
                .userId(event.getUserId())
                .userEmail(event.getUserEmail())
                .type(NotificationType.RESERVATION_CONFIRMATION)
                .status(NotificationStatus.PENDING)
                .qrCode(event.getQrCode())
                .retryCount(0)
                .build();
        
        notification = notificationRepository.save(notification);
        
        try {
            // Send email
            emailService.sendReservationConfirmation(
                    event.getUserEmail(),
                    event.getReservationId(),
                    event.getStallIds(),
                    event.getQrCode()
            );
            
            // Update notification status
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Successfully processed notification for reservation ID: {}", event.getReservationId());
            
        } catch (Exception e) {
            log.error("Failed to process notification for reservation ID: {}", event.getReservationId(), e);
            
            // Update notification status
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            notificationRepository.save(notification);
            
            throw new NotificationProcessingException(
                    "Failed to process notification for reservation " + event.getReservationId(), e);
        }
    }
    
    @Transactional
    public void retryFailedNotifications() {
        log.info("Retrying failed notifications");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        var failedNotifications = notificationRepository
                .findByStatusAndCreatedAtBefore(NotificationStatus.FAILED, cutoffTime);
        
        log.info("Found {} failed notifications to retry", failedNotifications.size());
        
        for (NotificationHistory notification : failedNotifications) {
            if (notification.getRetryCount() < 3) {
                try {
                    notification.setStatus(NotificationStatus.RETRYING);
                    notificationRepository.save(notification);
                    
                    // Retry logic here
                    log.info("Retrying notification ID: {}", notification.getId());
                    
                } catch (Exception e) {
                    log.error("Retry failed for notification ID: {}", notification.getId(), e);
                }
            }
        }
    }
}
