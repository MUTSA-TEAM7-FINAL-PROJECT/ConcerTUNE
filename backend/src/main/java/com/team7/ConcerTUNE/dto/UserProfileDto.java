package com.team7.ConcerTUNE.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor // @Builder와 함께 사용 시 필수
public class UserProfileDto {
    private Long id;
    private String username;
    private String role; // USER, ARTIST, ADMIN 등
    private String bio;
    private String profileImageUrl;

    // 추가적인 통계 정보 (예시)
    private int followersCount;
    private int followingCount;

    // 💡 장르 선호도 목록 (GenreDto 사용)
    private List<GenreDto> genrePreferences;


    public UserProfileDto(Long id, String username, String role, String bio, String profileImageUrl, int followersCount, int followingCount, List<GenreDto> genrePreferences) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.genrePreferences = genrePreferences;
    }
}