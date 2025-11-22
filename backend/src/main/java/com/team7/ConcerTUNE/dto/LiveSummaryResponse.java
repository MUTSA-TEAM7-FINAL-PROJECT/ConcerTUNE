package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Live;
import com.team7.ConcerTUNE.entity.LiveArtist;
import com.team7.ConcerTUNE.entity.LiveSchedule;
import com.team7.ConcerTUNE.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveSummaryResponse {

    private Long id;
    private String title;
    private String posterUrl;
    private String ticketUrl;
    private LocalDateTime ticketDateTime;

    private List<ArtistSummaryDto> artists;
    private List<LiveScheduleDto> schedules;

    private int countBookmark;
    private Boolean isBookmarked;

    // 로그인
    public static LiveSummaryResponse fromEntity(Live live, boolean isBookmarked) {
        return LiveSummaryResponse.builder()
                .id(live.getId())
                .title(live.getTitle())
                .posterUrl(live.getPosterUrl())
                .ticketUrl(live.getTicketUrl())
                .ticketDateTime(live.getTicketDateTime())
                .artists(
                        live.getLiveArtists() == null ? List.of()
                                : live.getLiveArtists().stream()
                                .map(link -> ArtistSummaryDto.fromEntity(link.getArtist()))
                                .toList()
                )
                .schedules(
                        live.getLiveSchedules() == null ? List.of()
                                : live.getLiveSchedules().stream()
                                .map(ls -> LiveScheduleDto.fromEntity(ls.getSchedule()))
                                .toList()
                )
                .countBookmark(
                        live.getBookmarks() == null ? 0 : live.getBookmarks().size()
                )
                .isBookmarked(isBookmarked)
                .build();
    }

    // 비로그인
    public static LiveSummaryResponse fromEntity(Live live) {
        return fromEntity(live, false);
    }

//    // 로그인
//    public static LiveSummaryResponse fromEntity(
//            Live entity,
//            List<ArtistSummaryDto> artists,
//            List<ScheduleDto> schedules,
//            int countBookmark,
//            Boolean isBookmarked
//    ) {
//        return LiveSummaryResponse.builder()
//                .id(entity.getId())
//                .title(entity.getTitle())
//                .posterUrl(entity.getPosterUrl())
//                .ticketUrl(entity.getTicketUrl())
//                .ticketDateTime(entity.getTicketDateTime())
//                .artists(artists)
//                .schedules(schedules)
//                .countBookmark(countBookmark)
//                .isBookmarked(isBookmarked)
//                .build();
//    }
}
