package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_requests",
        indexes = {
                @Index(name = "idx_pr_artist",  columnList = "artist_id"),
                // @Index(name = "idx_pr_user",    columnList = "user_id"),
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

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pr_artist"))
    private Artist artist;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", length = 20, nullable = false)
    private RequestStatus requestStatus = RequestStatus.PENDING;

    @Column(name = "request_created_at", nullable = false)
    private LocalDateTime requestCreatedAt;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

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
