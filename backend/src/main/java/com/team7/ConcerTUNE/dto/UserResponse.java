package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.AuthRole;
import com.team7.ConcerTUNE.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String profileImageUrl;
    private String bio;
    private AuthRole auth;
    private int followersCount;
    private int followingCount;

    private List<GenrePreferenceResponse> genrePreferences;

    public static UserResponse from(User user) {

        List<GenrePreferenceResponse> genres = user.getTags() == null
                ? Collections.emptyList()
                : IntStream.range(0, user.getTags().size())
                .mapToObj(i -> new GenrePreferenceResponse(
                        (long) i,
                        user.getTags().get(i)
                ))
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .profileImageUrl(user.getProfileImageUrl())
                .bio(user.getBio())
                .auth(user.getAuth())
                .genrePreferences(genres)
                .followersCount(user.getFollowers().size())
                .followingCount(user.getFollowings().size())
                .build();
    }
}
