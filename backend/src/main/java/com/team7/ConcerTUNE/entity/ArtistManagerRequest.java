package com.team7.ConcerTUNE.temp.entity;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.BaseEntity;
import com.team7.ConcerTUNE.entity.RequestStatus;
import com.team7.ConcerTUNE.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artist_manager_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ArtistManagerRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요청자 (관리 권한을 받고자 하는 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_am_req_user"))
    private User user;

    // 관리 권한을 요청하는 대상 Artist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_am_req_artist"))
    private Artist artist;

    // 요청 상태 (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "is_official_requested", nullable = false)
    private boolean isOfficial = false;

    // 요청자가 제출한 사유
    @Column(columnDefinition = "TEXT")
    private String reason;

    // 관리자(Admin)가 승인/반려 시 기록할 메모
    @Column(columnDefinition = "TEXT")
    private String adminNote;

    /** 요청을 승인 상태로 변경합니다. */
    public void approve(String note) {
        this.status = RequestStatus.APPROVED;
        this.adminNote = note;
    }

    /** 요청을 반려 상태로 변경합니다. */
    public void reject(String note) {
        this.status = RequestStatus.REJECTED;
        this.adminNote = note;
    }
}