package com.mvc.crud.repository;

import com.mvc.crud.model.Friend;
import com.mvc.crud.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // FIXED: changed receiver ➤ recipient
    List<Notification> findByRecipient(Friend recipient);

    List<Notification> findByRecipientOrderByTimestampDesc(Friend currentFriend);
}
