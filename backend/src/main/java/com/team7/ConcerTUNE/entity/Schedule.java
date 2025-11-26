package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id; // NotificationScheduler에서는 getId()를 사용하게 됨

    @Column(nullable = false)
    private String title; // [추가] 스케줄 제목

    @Column(name = "live_date", nullable = false)
    private LocalDate liveDate;

    @Column(name = "live_time", nullable = false)
    private LocalTime liveTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist; // [추가] 아티스트 연결
}