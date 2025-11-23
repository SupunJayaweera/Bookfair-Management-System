package com.bookfair.notification.controller;

import com.bookfair.notification.entity.NotificationHistory;
import com.bookfair.notification.entity.NotificationStatus;
import com.bookfair.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "notification-service");
        health.put("timestamp", System.currentTimeMillis());
        
        // Add notification stats
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", notificationRepository.count());
        stats.put("sent", notificationRepository.countByStatus(NotificationStatus.SENT));
        stats.put("failed", notificationRepository.countByStatus(NotificationStatus.FAILED));
        stats.put("pending", notificationRepository.countByStatus(NotificationStatus.PENDING));
        health.put("stats", stats);
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<NotificationHistory>> getNotificationsByReservation(
            @PathVariable Long reservationId) {
        List<NotificationHistory> notifications = notificationRepository.findByReservationId(reservationId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<NotificationHistory>> getNotificationsByEmail(@PathVariable String email) {
        List<NotificationHistory> notifications = notificationRepository.findByUserEmail(email);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationHistory>> getNotificationsByStatus(
            @PathVariable NotificationStatus status) {
        List<NotificationHistory> notifications = notificationRepository.findByStatus(status);
        return ResponseEntity.ok(notifications);
    }
}
