package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.AuthRole;
import com.team7.ConcerTUNE.entity.Genre;
import com.team7.ConcerTUNE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<User> findAllByAuth(AuthRole auth);

    // 아티스트의 장르들 중 하나라도 선호하는 유저를 중복 없이 조회
    @Query("SELECT DISTINCT ugp.user FROM UserGenrePreference ugp WHERE ugp.genre IN :genres")
    List<User> findUsersByPreferredGenres(@Param("genres") Set<Genre> genres);
}
