package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.*;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.repository.FollowRepository;
import com.team7.ConcerTUNE.repository.UserArtistRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import com.team7.ConcerTUNE.repository.BookmarkRepository;
import com.team7.ConcerTUNE.repository.GenreRepository;
import com.team7.ConcerTUNE.repository.PostRepository;
import com.team7.ConcerTUNE.repository.UserGenrePreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final BookmarkRepository bookmarksRepository;
    private final UserArtistRepository userArtistRepository;
    private final PostRepository postRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final FollowRepository followRepository;

    public List<Lives> getBookmarkedLives(Long userId) {
        return bookmarksRepository.findByUserId(userId)
                .stream()
                .map(Bookmarks::getLive)
                .collect(Collectors.toList());
    }

    public List<Artist> getFollowedArtists(Long userId) {
        return userArtistRepository.findByUserId(userId)
                .stream()
                .map(UserArtist::getArtist)
                .collect(Collectors.toList());
    }

    public List<Post> getMyPosts(Long userId) {
        return postRepository.findByWriterId(userId);
    }

    public List<LiveDto> getBookmarkedLivesDto(Long userId) {
        List<Lives> lives = getBookmarkedLives(userId); // 기존 엔티티 조회
        return lives.stream().map(live -> {
            LiveDto dto = new LiveDto();
            dto.setId(live.getId());
            dto.setTitle(live.getTitle());
            dto.setDescription(live.getDescription());
            dto.setPosterUrl(live.getPosterUrl());
            dto.setTicketUrl(live.getTicketUrl());
            dto.setVenue(live.getVenue());
            dto.setSeatPrices(live.getSeatPrices());

            List<LiveArtistDto> artists = live.getLiveArtists().stream().map(la -> {
                LiveArtistDto ladto = new LiveArtistDto();
                ladto.setId(la.getId());
                ladto.setArtistName(la.getArtist().getArtistName());
                return ladto;
            }).collect(Collectors.toList());

            dto.setLiveArtists(artists);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ArtistDto> getFollowedArtistsDto(Long userId) {
        List<Artist> artists = getFollowedArtists(userId);
        return artists.stream().map(a -> {
            ArtistDto dto = new ArtistDto();
            dto.setId(a.getArtistId());
            dto.setName(a.getArtistName());
            dto.setProfileImageUrl(a.getArtistImageUrl());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<PostDto> getMyPostsDto(Long userId) {
        List<Post> posts = getMyPosts(userId);
        return posts.stream().map(p -> {
            PostDto dto = new PostDto();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setContent(p.getContent());
            dto.setCreatedAt(p.getCreatedAt().toString()); // 필요 시 포맷 변경
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 1. 사용자 기본 정보 (이름, Bio) 업데이트
        user.setUsername(dto.getUsername());
        user.setBio(dto.getBio());

        updateUserGenrePreferences(user, dto.getGenreIds());
    }

    private void updateUserGenrePreferences(User user, List<Long> newGenreIds) {
        // 기존 선호도 전체 삭제 ( 엔티티는 복합키를 사용하므로 간편하게 삭제 후 재등록)
        userGenrePreferenceRepository.deleteAllByUserId(user.getId());

        if (newGenreIds == null || newGenreIds.isEmpty()) {
            return;
        }

        // 새로운 장르 ID 목록으로 장르 엔티티 조회
        List<Genre> genres = genreRepository.findAllById(newGenreIds);

        // 새로운 선호도 엔티티 생성 및 저장
        List<UserGenrePreference> newPreferences = genres.stream()
                .map(genre -> UserGenrePreference.builder()
                        .user(user)
                        .genre(genre)
                        .build())
                .collect(Collectors.toList());

        userGenrePreferenceRepository.saveAll(newPreferences);
    }

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<UserGenrePreference> preferences = userGenrePreferenceRepository.findByUserId(userId);

        List<GenreDto> genreDtos = preferences.stream()
                .map(ugp -> new GenreDto(ugp.getGenre().getGenreId(), ugp.getGenre().getGenreName()))
                .collect(Collectors.toList());

        // 팔로워/팔로잉 수 조회 (예: followRepository 사용)
        int followersCount = followRepository.countByFollowingId(userId); // 나를 팔로우한 수
        int followingCount = followRepository.countByFollowerId(userId); // 내가 팔로우한 수

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getAuth().name())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .genrePreferences(genreDtos)
                .build();
    }


    @Transactional
    public void updateProfileImage(Long userId, String profileImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        user.setProfileImageUrl(profileImageUrl);
    }
}