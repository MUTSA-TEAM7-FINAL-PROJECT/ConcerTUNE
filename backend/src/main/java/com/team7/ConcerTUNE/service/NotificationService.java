package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.NotificationDto;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.exception.BadRequestException;
import com.team7.ConcerTUNE.exception.ResourceNotFoundException;
import com.team7.ConcerTUNE.repository.NotificationRepository;
import com.team7.ConcerTUNE.repository.UserArtistRepository;
import com.team7.ConcerTUNE.repository.UserNotificationRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import com.team7.ConcerTUNE.security.SimpleUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final UserArtistRepository userArtistRepository;

    // 내 알림 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotifications(Authentication authentication, String isReadParam) {
        User user = getUserFromAuth(authentication);
        List<UserNotification> userNotifs;

        // [수정] 모든 호출부 변경
        if ("true".equalsIgnoreCase(isReadParam)) {
            userNotifs = userNotificationRepository.findByUserAndIsReadOrderByIdDesc(user, true); // [수정]
        } else if ("all".equalsIgnoreCase(isReadParam)) {
            List<UserNotification> unread = userNotificationRepository.findByUserAndIsReadOrderByIdDesc(user, false); // [수정]
            List<UserNotification> read = userNotificationRepository.findAllByUserAndIsRead(user, true);
            unread.addAll(read);
            userNotifs = unread;
        } else {
            userNotifs = userNotificationRepository.findByUserAndIsReadOrderByIdDesc(user, false); // [수정]
        }

        return userNotifs.stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 알림 읽음 처리
    public void markAsRead(Long userNotificationId, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        UserNotification userNotif = userNotificationRepository.findById(userNotificationId)
                .orElseThrow(() -> new ResourceNotFoundException("알림을 찾을 수 없습니다. ID: " + userNotificationId));

        if (!userNotif.getUser().getId().equals(user.getId())) {
            log.warn("유저(ID:{})가 타인(ID:{})의 알림(ID:{})에 접근 시도", user.getId(), userNotif.getUser().getId(), userNotificationId);
            throw new BadRequestException("자신의 알림만 읽음 처리할 수 있습니다");
        }
        if (!userNotif.isRead()) {
            userNotif.setRead(true);
            userNotificationRepository.save(userNotif);
        }
    }

    // 모든 알림 읽음 처리
    public void markAllAsRead(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        List<UserNotification> unreadNotifications = userNotificationRepository.findAllByUserAndIsRead(user, false);
        if (unreadNotifications.isEmpty()) {
            return;
        }
        for (UserNotification notif : unreadNotifications) {
            notif.setRead(true);
        }
        userNotificationRepository.saveAll(unreadNotifications);
    }

    // 내부용 알림 생성 로직 - 특정 유저 1명에게 알림 생성
    public void createNotification(User recipient, String content, String link) {
        if (recipient == null) {
            log.warn("알림 수신자(recipient)가 null입니다.");
            return;
        }

        Notification notification = Notification.builder()
                .content(content)
                .link(link)
                .build();
        notificationRepository.save(notification);

        UserNotification userNotification = UserNotification.builder()
                .user(recipient)
                .notification(notification)
                .isRead(false)
                .build();
        userNotificationRepository.save(userNotification);
    }

    // 내부용 알림 생성 로직 - 특정 아티스트를 팔로우하는 모든 유저들에게 알림 생성 (Async 권장)
    @Async
    public void createNotificationForArtistFollowers(Artist artist, String content, String link) {
        Notification notification = Notification.builder()
                .content(content)
                .link(link)
                .build();
        notificationRepository.save(notification);

        List<User> followers = userArtistRepository.findAllByArtist(artist).stream()
                .map(UserArtist::getUser)
                .toList();

        if (followers.isEmpty()) {
            log.info("아티스트(ID:{})에게 팔로워가 없어 알림을 생성하지 않습니다.", artist.getArtistId());
            return;
        }

        List<UserNotification> userNotifications = followers.stream()
                .map(user -> UserNotification.builder()
                        .user(user)
                        .notification(notification)
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        userNotificationRepository.saveAll(userNotifications);
        log.info("아티스트(ID:{})의 팔로워 {}명에게 알림을 일괄 생성했습니다.", artist.getArtistId(), followers.size());
    }

    // [추가] 장르 기반 추천 알림 발송 (피드백 반영)
    @Async
    public void sendGenreBasedNotification(Artist artist, String content, String link) {
        // 1. 아티스트의 장르 목록 추출
        Set<Genre> genres = artist.getArtistGenres().stream()
                .map(ArtistGenre::getGenre)
                .collect(Collectors.toSet());

        if (genres.isEmpty()) {
            return;
        }

        // 2. 해당 장르를 선호하는 유저 조회
        List<User> targetUsers = userRepository.findUsersByPreferredGenres(genres);

        if (targetUsers.isEmpty()) {
            log.info("장르({})를 선호하는 유저가 없어 추천 알림을 보내지 않습니다.", genres.size());
            return;
        }

        // 3. 알림 생성 및 전송
        Notification notification = Notification.builder()
                .content(content)
                .link(link)
                .build();
        notificationRepository.save(notification);

        List<UserNotification> userNotifications = targetUsers.stream()
                .map(user -> UserNotification.builder()
                        .user(user)
                        .notification(notification)
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        userNotificationRepository.saveAll(userNotifications);

        log.info("장르 기반 추천 알림: 유저 {}명에게 전송 완료", targetUsers.size());
    }

    // 내부용 알림 생성 로직 - 모든 관리자에게 알림 생성
    public void createNotificationForAdmins(String content, String link) {
        List<User> admins = userRepository.findAllByAuth(AuthRole.ADMIN);
        if (admins.isEmpty()) {
            log.warn("관리자 유저가 없어 알림을 생성하지 못했습니다.");
            return;
        }

        Notification notification = Notification.builder()
                .content(content)
                .link(link)
                .build();
        notificationRepository.save(notification);

        List<UserNotification> adminNotifications = admins.stream()
                .map(admin -> UserNotification.builder()
                        .user(admin)
                        .notification(notification)
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        userNotificationRepository.saveAll(adminNotifications);
        log.info("관리자 {}명에게 알림을 일괄 생성했습니다.", admins.size());
    }

    // 유틸리티 메서드
    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SimpleUserDetails)) {
            throw new BadRequestException("유효한 로그인 정보가 없습니다.");
        }
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("인증 정보에 해당하는 유저를 찾을 수 없습니다. ID: " + userDetails.getUserId()));
    }
}