package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedules {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "schedule_id")
  private Long id;

  @Column(name = "live_date", nullable = false)
  private LocalDateTime liveDate;

  @Column(name = "live_time", nullable = false)
  private LocalDateTime liveTime;

  @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LiveSchedules> liveSchedules = new ArrayList<>();
}