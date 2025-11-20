package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.temp.dto.*;
import com.team7.ConcerTUNE.temp.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/{userId}/contents")
    public Map<String, Object> getUserContents(@PathVariable Long userId) {
        List<LiveDto> bookmarkedLives = myPageService.getBookmarkedLivesDto(userId);
        List<ArtistDto> followedArtists = myPageService.getFollowedArtistsDto(userId);
        List<PostDto> myPosts = myPageService.getMyPostsDto(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("bookmarkedLives", bookmarkedLives);
        response.put("followedArtists", followedArtists);
        response.put("myPosts", myPosts);

        return response;
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateDto dto) {
        myPageService.updateProfile(userId, dto);
        return ResponseEntity.ok().build(); // 200 OK 응답
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable Long userId) {
        UserProfileDto profile = myPageService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}/profile/image")
    public ResponseEntity<Void> updateProfileImage(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        String profileImageUrl = body.get("profileImageUrl");
        myPageService.updateProfileImage(userId, profileImageUrl);
        return ResponseEntity.ok().build();
    }
}