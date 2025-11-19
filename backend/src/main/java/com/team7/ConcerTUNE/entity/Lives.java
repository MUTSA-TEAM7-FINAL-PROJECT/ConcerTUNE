package com.team7.ConcerTUNE.entity;

import com.team7.ConcerTUNE.util.JsonToMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "lives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lives extends BaseEntity {

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

  @Convert(converter = JsonToMapConverter.class)
  @Column(name = "seat_prices", columnDefinition = "TEXT")
  private Map<String, Integer> seatPrices = new HashMap<>();

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LiveArtist> liveArtists = new ArrayList<>();

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LiveSchedules> liveSchedules = new ArrayList<>();

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Bookmarks> bookmarks = new ArrayList<>();
}