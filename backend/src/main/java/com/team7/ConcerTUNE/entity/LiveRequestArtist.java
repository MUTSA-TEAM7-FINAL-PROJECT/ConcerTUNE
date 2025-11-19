package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "live_request_artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveRequestArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_request_id", nullable = false)
    private LiveRequest liveRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
}

