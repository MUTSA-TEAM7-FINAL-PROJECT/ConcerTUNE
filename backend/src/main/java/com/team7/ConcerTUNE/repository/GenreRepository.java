package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
