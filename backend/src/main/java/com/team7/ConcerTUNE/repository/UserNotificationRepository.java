// ConcerTUNE-develop/backend/src/main/java/com/team7/ConcerTUNE/repository/UserNotificationRepository.java

package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByUserAndIsReadOrderByIdDesc(User user, boolean isRead);
    List<UserNotification> findAllByUserAndIsRead(User user, boolean isRead);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification un SET un.isRead = true WHERE un.user = :user AND un.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);
}