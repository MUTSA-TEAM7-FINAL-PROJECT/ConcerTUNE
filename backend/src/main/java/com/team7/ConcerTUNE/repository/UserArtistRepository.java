package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.entity.UserArtist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserArtistRepository extends JpaRepository<UserArtist, UserArtist> {
    boolean existsByUserAndArtist(User user, Artist artist);

    Optional<UserArtist> findByUserAndArtist(User user, Artist artist);

    List<UserArtist> findAllByArtist(Artist artist);

    long countByArtist(Artist artist);

    List<UserArtist> findByUserId(Long userId);

    @Query("SELECT ua FROM UserArtist ua WHERE ua.user.id = :userId AND ua.artist.artistId = :artistId")
    Optional<UserArtist> findByUserIdAndArtistId(@Param("userId") Long userId, @Param("artistId") Long artistId);

}
