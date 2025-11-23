package com.bookfair.notification.repository;

import com.bookfair.notification.entity.NotificationHistory;
import com.bookfair.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationHistory, Long> {
    
    List<NotificationHistory> findByReservationId(Long reservationId);
    
    List<NotificationHistory> findByStatus(NotificationStatus status);
    
    List<NotificationHistory> findByUserEmail(String userEmail);
    
    List<NotificationHistory> findByStatusAndCreatedAtBefore(
        NotificationStatus status, 
        LocalDateTime dateTime
    );
    
    long countByStatus(NotificationStatus status);
}
