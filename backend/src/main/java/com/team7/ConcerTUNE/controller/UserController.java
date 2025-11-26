package com.team7.ConcerTUNE.controller;

import com.team7.ConcerTUNE.dto.UserFollowResponse;
import com.team7.ConcerTUNE.dto.UserResponse;
import com.team7.ConcerTUNE.dto.UserUpdateRequest;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.security.SimpleUserDetails;
import com.team7.ConcerTUNE.service.FollowService;
import com.team7.ConcerTUNE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FollowService followService;

    //내 프로필 보기
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal SimpleUserDetails principal) {
        User user = userService.findEntityById(principal.getUserId());
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
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal SimpleUserDetails principal,
            @RequestBody UserUpdateRequest request
    ) {
        User user = userService.findEntityById(principal.getUserId());
        return ResponseEntity.ok(userService.updateMyProfile(user, request));
    }


    //내 프로필 이미지 등록
    @PatchMapping("/me/profile-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> uploadProfileImage(
            @AuthenticationPrincipal SimpleUserDetails principal,
            @RequestParam("file") MultipartFile imageFile) throws IOException {
        User user = userService.findEntityById(principal.getUserId());
        return ResponseEntity.ok(userService.uploadProfileImage(user, imageFile));
    }

    //내 프로필 이미지 삭제
    @DeleteMapping("/me/profile-image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> deleteProfileImage(@AuthenticationPrincipal SimpleUserDetails principal) throws IOException {
        User user = userService.findEntityById(principal.getUserId());
        return ResponseEntity.ok(userService.deleteProfileImage(user));
    }

    //계정 삭제
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal SimpleUserDetails principal) {
        User user = userService.findEntityById(principal.getUserId());
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }

    //팔로우, 언팔로우
    @PostMapping("/{targetId}/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> toggleFollow(
            @AuthenticationPrincipal SimpleUserDetails principal,
            @PathVariable Long targetId) {
        User follower = userService.findEntityById(principal.getUserId());
        followService.toggleFollow(follower, targetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{targetId}/is-following")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> isFollowing(
            @AuthenticationPrincipal SimpleUserDetails principal,
            @PathVariable Long targetId
    ) {
        User me = userService.findEntityById(principal.getUserId());
        boolean isFollowing = followService.isFollowing(me, targetId);
        return ResponseEntity.ok(isFollowing);
    }


    //팔로워 유저들 조회
    @GetMapping("/me/followers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<UserFollowResponse>> getMyFollowers(
            @AuthenticationPrincipal SimpleUserDetails principal,
            Pageable pageable) {
        User user = userService.findEntityById(principal.getUserId());
        return ResponseEntity.ok(followService.getFollowers(user, pageable));
    }

    //팔로잉 유저들 조회
    @GetMapping("/me/following")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<UserFollowResponse>> getMyFollowing(
            @AuthenticationPrincipal SimpleUserDetails principal,
            Pageable pageable) {
        User user = userService.findEntityById(principal.getUserId());
        return ResponseEntity.ok(followService.getFollowings(user, pageable));
    }

    // 특정 유저의 팔로워 조회
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<UserFollowResponse>> getFollowersOfUser(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        User target = userService.findEntityById(userId);
        return ResponseEntity.ok(followService.getFollowers(target, pageable));
    }

    // 특정 유저의 팔로잉 조회
    @GetMapping("/{userId}/followings")
    public ResponseEntity<Page<UserFollowResponse>> getFollowingsOfUser(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        User target = userService.findEntityById(userId);
        return ResponseEntity.ok(followService.getFollowings(target, pageable));
    }
}
