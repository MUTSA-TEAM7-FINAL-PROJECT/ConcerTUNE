package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.UserGenrePreference;
import com.team7.ConcerTUNE.entity.UserGenrePreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGenrePreferenceRepository extends JpaRepository<UserGenrePreference, UserGenrePreferenceId> {

    @Query("SELECT ugp.genre.genreId FROM UserGenrePreference ugp WHERE ugp.user.id = :userId")
    List<Long> findGenreIdsByUserId(@Param("userId") Long userId);
}