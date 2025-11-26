package com.team7.ConcerTUNE.scheduler;

import com.team7.ConcerTUNE.entity.Schedule;
import com.team7.ConcerTUNE.repository.ScheduleRepository;
import com.team7.ConcerTUNE.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    // 매일 오전 9시에 실행
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional(readOnly = true)
    public void notifyUpcomingSchedules() {
        log.info("공연 임박 알림 스케줄러 시작");

        // 내일 날짜 구하기
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // [수정] 날짜 기준으로 조회하도록 변경
        List<Schedule> upcomingSchedules = scheduleRepository.findAllByLiveDate(tomorrow);

        for (Schedule schedule : upcomingSchedules) {
            if (schedule.getArtist() == null) continue;

            String artistName = schedule.getArtist().getArtistName();
            String title = schedule.getTitle(); // [수정] 필드 추가됨

            String content = "⏳ 내일! " + artistName + "의 공연 [" + title + "]이 예정되어 있어요.";
            // [수정] getScheduleId() -> getId() (Lombok 기본 Getter)
            String link = "/schedules/" + schedule.getId();

            notificationService.createNotificationForArtistFollowers(schedule.getArtist(), content, link);
        }

        log.info("총 {}개의 임박 공연에 대한 알림 발송 완료", upcomingSchedules.size());
    }
}