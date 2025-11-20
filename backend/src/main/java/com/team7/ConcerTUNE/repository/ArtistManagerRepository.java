package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.ArtistManager;
import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtistManagerRepository extends JpaRepository<ArtistManager, Long> {
    Optional<ArtistManager> findByIdUserIdAndIdArtistId(Long userId, Long artistId);
    Optional<ArtistManager> findByUserAndArtist(User user, Artist artist);
    @Query("SELECT CASE WHEN COUNT(am) > 0 THEN true ELSE false END " +
            "FROM ArtistManager am " +
            "WHERE am.artist.id = :artistId AND am.user.id = :userId")
    boolean existsByArtistIdAndUserId(@Param("artistId") Long artistId, @Param("userId") Long userId);
}
