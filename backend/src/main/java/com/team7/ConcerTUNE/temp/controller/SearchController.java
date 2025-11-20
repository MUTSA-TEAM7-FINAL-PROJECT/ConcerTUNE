package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.temp.dto.SearchResponseDto;
import com.team7.ConcerTUNE.temp.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<Page<SearchResponseDto>> searchAll(
            @RequestParam("q") String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<SearchResponseDto> results = searchService.searchAll(keyword, pageable);

        return ResponseEntity.ok(results);
    }
}