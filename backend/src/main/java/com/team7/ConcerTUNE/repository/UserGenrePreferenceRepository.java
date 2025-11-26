package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.UserGenrePreference;
import com.team7.ConcerTUNE.entity.UserGenrePreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGenrePreferenceRepository extends JpaRepository<UserGenrePreference, UserGenrePreferenceId> {

    @Query("SELECT ugp.genre.genreId FROM UserGenrePreference ugp WHERE ugp.user.id = :userId")
    List<Long> findGenreIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserGenrePreference ugp WHERE ugp.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Query("SELECT ugp FROM UserGenrePreference ugp JOIN FETCH ugp.genre WHERE ugp.user.id = :userId")
    List<UserGenrePreference> findByUserId(@Param("userId") Long userId);
}