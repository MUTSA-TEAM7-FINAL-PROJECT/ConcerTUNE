package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "live_requests",
        indexes = {
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

    @Column(name = "price", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String,Integer> price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pr_user"))
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "liveRequest", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<LiveRequestArtist> liveRequestArtists = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "liveRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiveRequestSchedule> liveRequestSchedules = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", length = 20, nullable = false)
    private RequestStatus requestStatus = RequestStatus.PENDING;

    @Column(name = "request_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status_updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = createdAt;
    }

    public void changeStatus(RequestStatus newStatus) {
        this.requestStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
