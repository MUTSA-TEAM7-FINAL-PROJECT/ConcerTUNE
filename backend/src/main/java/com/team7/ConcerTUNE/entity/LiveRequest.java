package com.team7.ConcerTUNE.entity;

import com.team7.ConcerTUNE.dto.NewArtistRequestDto;
import com.team7.ConcerTUNE.dto.ScheduleDto;
import com.team7.ConcerTUNE.util.JsonToLongListConverter;
import com.team7.ConcerTUNE.util.JsonToMapConverter;
import com.team7.ConcerTUNE.util.JsonToNewArtistListConverter;
import com.team7.ConcerTUNE.util.JsonToScheduleListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Entity
@Table(name = "live_requests",
        indexes = {
                @Index(name = "idx_pr_requester", columnList = "requester_id"),
                @Index(name = "idx_pr_status",  columnList = "request_status"),
                @Index(name = "idx_pr_created", columnList = "request_created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "ticket_url", length = 500)
    private String ticketUrl;

    @Column(name = "venue", length = 200)
    private String venue;

    @Convert(converter = JsonToMapConverter.class)
    @Column(name = "seat_prices", columnDefinition = "TEXT")
    private Map<String, Integer> seatPrices = new HashMap<>();

    @Convert(converter = JsonToLongListConverter.class)
    @Column(name = "artist_ids", columnDefinition = "TEXT")
    private List<Long> artistIds = new ArrayList<>();

    @Convert(converter = JsonToNewArtistListConverter.class)
    @Column(name = "new_artist_requests_data", columnDefinition = "TEXT")
    private List<NewArtistRequestDto> newArtistRequestData = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_live_request_requester"))
    private User requester;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", length = 20, nullable = false)
    private RequestStatus requestStatus = RequestStatus.PENDING;

    @Column(name = "request_created_at", nullable = false)
    private LocalDateTime requestCreatedAt;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @Convert(converter = JsonToScheduleListConverter.class)
    @Column(name = "requested_schedules", columnDefinition = "TEXT")
    private List<ScheduleDto> requestedSchedules = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (requestCreatedAt == null) requestCreatedAt = LocalDateTime.now();
        if (statusUpdatedAt == null)  statusUpdatedAt  = requestCreatedAt;
    }

    public void changeStatus(RequestStatus newStatus) {
        this.requestStatus = newStatus;
        this.statusUpdatedAt = LocalDateTime.now();
    }
}