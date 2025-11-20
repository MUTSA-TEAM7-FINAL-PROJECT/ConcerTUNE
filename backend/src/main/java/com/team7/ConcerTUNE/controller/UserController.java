package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.UserFollowResponse;
import com.team7.ConcerTUNE.dto.UserResponse;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.service.FollowService;
import com.team7.ConcerTUNE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FollowService followService;

    //내 프로필 보기
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getMyProfile(user));
    }

    //유저 프로필 보기
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    //내 프로필 수정
    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.updateMyProfile(user));
    }

    //내 프로필 이미지 등록
    @PostMapping("/me/profile-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> uploadProfileImage(
            @AuthenticationPrincipal User user,
            @RequestParam("image") MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok(userService.uploadProfileImage(user, imageFile));
    }

    //내 프로필 이미지 삭제
    @DeleteMapping("/me/profile-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> deleteProfileImage(@AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.ok(userService.deleteProfileImage(user));
    }

    //계정 삭제
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{targetId}/is-following")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            Authentication authentication,
            @PathVariable Long targetId) {
        boolean following = followService.isFollowing(authentication, targetId);
        return ResponseEntity.ok(Map.of("isFollowing", following));
    }

    //팔로우, 언팔로우
    @PostMapping("/{targetId}/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Boolean>> toggleFollow(
            Authentication authentication,
            @PathVariable Long targetId) {
        boolean following = followService.toggleFollow(authentication, targetId);
        return ResponseEntity.ok(Map.of("isFollowing", following));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<UserFollowResponse>> getFollowers(
            @PathVariable Long userId,
            Authentication authentication,
            Pageable pageable) {

        Page<UserFollowResponse> followers = followService.getFollowersByUserId(userId, pageable);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<Page<UserFollowResponse>> getFollowings(
            @PathVariable Long userId,
            Authentication authentication,
            Pageable pageable) {

        Page<UserFollowResponse> followings = followService.getFollowingsByUserId(userId, pageable);
        return ResponseEntity.ok(followings);
    }
}
