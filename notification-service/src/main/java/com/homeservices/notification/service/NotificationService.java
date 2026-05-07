package com.homeservices.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.homeservices.notification.entity.Notification;
import com.homeservices.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Long userId, String userType, String notificationType,
                                           String message, Long bookingId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setUserType(userType);
        notification.setNotificationType(notificationType);
        notification.setMessage(message);
        notification.setBookingId(bookingId);
        notification.setIsRead(false);
        notification.setCreatedDate(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUserUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, false);
    }

    public List<Notification> getNotificationsByUserType(String userType) {
        return notificationRepository.findByUserType(userType);
    }

    public List<Notification> getUnreadNotificationsByUserType(String userType) {
        return notificationRepository.findByUserTypeAndIsRead(userType, false);
    }

    public List<Notification> getBookingNotifications(Long bookingId) {
        return notificationRepository.findByBookingId(bookingId);
    }

    public Notification markAsRead(Long notificationId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification not found"));
        notification.setIsRead(true);
        notification.setReadDate(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        notifications.forEach(n -> {
            n.setIsRead(true);
            n.setReadDate(LocalDateTime.now());
        });
        notificationRepository.saveAll(notifications);
    }

    public Notification getNotificationById(Long notificationId) throws Exception {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification not found"));
    }
}
