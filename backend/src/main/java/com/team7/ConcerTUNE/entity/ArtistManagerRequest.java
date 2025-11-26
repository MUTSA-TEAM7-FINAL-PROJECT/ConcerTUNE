package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artist_manager_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistManagerRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요청한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 관리하고자 하는 아티스트 이름 (아직 아티스트가 등록 안 된 경우 대비)
    @Column(nullable = false)
    private String artistName;

    // [중요] 증빙 서류/포트폴리오 링크 (피드백 반영: 엄격한 검증)
    @Column(name = "proof_document_url", nullable = false, length = 500)
    private String proofDocumentUrl;

    // 소개글
    @Column(columnDefinition = "TEXT")
    private String description;

    // 승인 상태 (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    // 관리자 거절 사유 등 메모
    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "target_artist_id")
    private Long artistId;
}