package com.homeservices.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.homeservices.notification.entity.Notification;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);
    List<Notification> findByUserType(String userType);
    List<Notification> findByUserTypeAndIsRead(String userType, Boolean isRead);
    List<Notification> findByBookingId(Long bookingId);
}
