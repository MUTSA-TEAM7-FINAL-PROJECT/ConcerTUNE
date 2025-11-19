package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "lives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Live extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "live_id")
  private Long id;

  @Column(length = 200, nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "poster_url", length = 200)
  private String posterUrl;

  @Column(name = "ticket_url", length = 200)
  private String ticketUrl;

  @Column(length = 200)
  private String venue;

  @Column(name = "price", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String,Integer> price;

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LiveArtist> liveArtists;

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LiveSchedule> liveSchedules;
}