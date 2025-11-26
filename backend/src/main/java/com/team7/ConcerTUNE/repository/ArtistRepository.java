package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.dto.ArtistSummaryDto;
import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Page<Artist> findByArtistNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Artist> findByManager(User manager);
    Optional<Artist> findByArtistName(String artistName);

    // [N+1 해결] 전체 아티스트 목록 + 팔로워 수 조회
    @Query("SELECT new com.team7.ConcerTUNE.dto.ArtistSummaryDto(" +
            "a.artistId, a.artistName, a.artistImageUrl, COUNT(ua)) " +
            "FROM Artist a " +
            "LEFT JOIN UserArtist ua ON ua.artist = a " +
            "GROUP BY a.artistId, a.artistName, a.artistImageUrl")
    Page<ArtistSummaryDto> findAllWithFollowerCount(Pageable pageable);

    // [N+1 해결] 이름 검색 + 팔로워 수 조회
    @Query("SELECT new com.team7.ConcerTUNE.dto.ArtistSummaryDto(" +
            "a.artistId, a.artistName, a.artistImageUrl, COUNT(ua)) " +
            "FROM Artist a " +
            "LEFT JOIN UserArtist ua ON ua.artist = a " +
            "WHERE LOWER(a.artistName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "GROUP BY a.artistId, a.artistName, a.artistImageUrl")
    Page<ArtistSummaryDto> searchByNameWithFollowerCount(@Param("name") String name, Pageable pageable);
}
