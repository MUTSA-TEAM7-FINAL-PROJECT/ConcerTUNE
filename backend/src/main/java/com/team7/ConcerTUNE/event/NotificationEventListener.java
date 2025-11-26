package com.team7.ConcerTUNE.event;

import com.team7.ConcerTUNE.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleFollow(FollowEvent event) {
        String content = event.getFollower().getUsername() + "님이 당신을 팔로우했습니다.";
        String link = "/user/" + event.getFollower().getId();
        notificationService.createNotification(event.getTarget(), content, link);
        log.info("팔로우 알림 생성: {} -> {}", event.getFollower().getId(), event.getTarget().getId());
    }

    @EventListener
    public void handleArtistManagerRequest(ArtistManagerRequestEvent event) {
        String status = event.isApproved() ? "승낙" : "거절";
        String content = "아티스트 관리자 건이 " + status + "되었습니다.";
        String link = "/artist-manager/requests-list";
        notificationService.createNotification(event.getUser(), content, link);
        log.info("아티스트 관리자 알림 생성: 사용자ID={}", event.getUser().getId());
    }

    @EventListener
    public void handleComment(CommentCreatedEvent event) {
        String content = event.getCommenter() + "님이 게시글에 댓글을 달았습니다.";
        String link = "/post/" + event.getPost().getId();
        notificationService.createNotification(event.getPost().getWriter(), content, link);
        log.info("댓글 알림 생성: PostID={}, WriterID={}", event.getPost().getId(), event.getPost().getWriter().getId());
    }

    @EventListener
    public void handleLiveRequest(LiveRequestEvent event) {
        String status = event.isApproved() ? "승낙" : "거절";
        String content = "라이브 요청이 " + status + "되었습니다.";
        String link = "/concerts/request-list";
        notificationService.createNotification(event.getLiveRequest().getRequester(), content, link);
        log.info("라이브 요청 알림 생성: 요청ID={}", event.getLiveRequest().getRequestId());
    }

    @EventListener
    public void handleLiveCreated(LiveCreatedEvent event) {
        String content = "아티스트의 공연이 등록되었습니다.";
        String link = "/concerts/" + event.getLiveId();
        notificationService.createNotificationForArtistFollowers(event.getArtist(), content, link);
        log.info("공연 알림 생성: LiveID={}, ArtistID={}", event.getLiveId(), event.getArtist().getArtistId());
    }

    @EventListener
    public void handleSubscription(SubscriptionCreatedEvent event) {
        String content = "구독이 완료되었습니다.";
        notificationService.createNotification(event.getUser(), content, null);
        log.info("구독 알림 생성: UserID={}", event.getUser().getId());
    }
}
