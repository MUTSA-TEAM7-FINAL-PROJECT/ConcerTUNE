package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.repository.ArtistRepository;
import com.team7.ConcerTUNE.repository.LivesRepository;
import com.team7.ConcerTUNE.dto.SearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final LivesRepository livesRepository;
    private final ArtistRepository artistRepository;

    public Page<SearchResponseDto> searchAll(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        List<Lives> allLiveResults = livesRepository.findByTitleContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();

        List<Artist> allArtistResults = artistRepository.findByArtistNameContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();

        List<SearchResponseDto> combinedResults = Stream.concat(
                        allLiveResults.stream().map(SearchResponseDto::fromLive),
                        allArtistResults.stream().map(SearchResponseDto::fromArtist)
                )
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedResults.size());

        List<SearchResponseDto> pagedContent;
        if (start > combinedResults.size()) {
            pagedContent = List.of();
        } else {
            pagedContent = combinedResults.subList(start, end);
        }

        return new PageImpl<>(pagedContent, pageable, combinedResults.size());
    }
}