package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

  @Column(nullable = false)
  private Integer price;

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LiveArtist> liveArtists = new ArrayList<>();

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LiveSchedules> liveSchedules = new ArrayList<>();

  @OneToMany(mappedBy = "live", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Bookmarks> bookmarks = new ArrayList<>();
}