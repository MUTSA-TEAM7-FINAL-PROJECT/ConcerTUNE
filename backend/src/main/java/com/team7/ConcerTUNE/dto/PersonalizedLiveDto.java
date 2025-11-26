package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Lives;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PersonalizedLiveDto {

    private Long liveId;
    private String title;
    private String artistNames;
    private String genreName;
    private long dDay;

    public PersonalizedLiveDto(Lives live) {
        this.liveId = live.getId();
        this.title = live.getTitle();

        Optional<LocalDate> earliestScheduleDate = live.getLiveSchedules().stream()
                .map(liveSchedule -> liveSchedule.getSchedule().getLiveDate())
                .min(Comparator.naturalOrder());

        if (earliestScheduleDate.isPresent()) {
            this.dDay = ChronoUnit.DAYS.between(LocalDate.now(), earliestScheduleDate.get());
        } else {
            // 일정이 없는 경우
            this.dDay = -1;
        }

        this.artistNames = live.getLiveArtists().stream()
                .map(la -> la.getArtist().getArtistName())
                .collect(Collectors.joining(", "));

        this.genreName = live.getLiveArtists().stream()
                .flatMap(la -> la.getArtist().getArtistGenres().stream())
                .map(ag -> ag.getGenre().getGenreName())
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.joining(", "));

        // 장르가 없는 경우를 대비한 기본값 설정 (선택 사항)
        if (this.genreName.isEmpty()) {
            this.genreName = "장르 정보 없음";
        }
    }
}