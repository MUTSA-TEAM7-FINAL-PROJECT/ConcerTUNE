package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.temp.dto.FollowArtistFeedDto;
import com.team7.ConcerTUNE.temp.service.FollowFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/feeds")
@RequiredArgsConstructor
public class FollowFeedController {

    private final FollowFeedService followFeedService;


    @GetMapping("/follow")
    public ResponseEntity<List<FollowArtistFeedDto>> getFollowArtistFeeds(Authentication authentication) {
        List<FollowArtistFeedDto> feeds = followFeedService.getFollowArtistFeeds(authentication);
        return ResponseEntity.ok(feeds);
    }
}