package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.ArtistDetailDto;
import com.team7.ConcerTUNE.dto.ArtistSummaryDto;
import com.team7.ConcerTUNE.dto.GenreDto;
import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.ArtistGenre;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.entity.UserArtist;
import com.team7.ConcerTUNE.exception.ResourceNotFoundException;
import com.team7.ConcerTUNE.repository.*;
import com.team7.ConcerTUNE.temp.dto.ArtistDetailResponse;
import com.team7.ConcerTUNE.temp.dto.FollowStatusResponse;
import com.team7.ConcerTUNE.temp.dto.NewArtistRequestDto;
import com.team7.ConcerTUNE.temp.repository.ArtistGenreRepository;
import com.team7.ConcerTUNE.temp.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final UserArtistRepository userArtistRepository;
    private final NotificationService notificationService;
    private final ArtistManagerRepository artistManagerRepository;
    private final AuthService authService;
    private final LivesRepository liveRepository;
    private final GenreRepository genreRepository;
    private final ArtistGenreRepository artistGenreRepository;

    // 아티스트 목록 조회
    @Transactional(readOnly = true)
    public Page<ArtistSummaryDto> getArtistList(String name, Pageable pageable) {
        Page<Artist> artistPage;
        if (StringUtils.hasText(name)) {
            artistPage = artistRepository.findByArtistNameContainingIgnoreCase(name, pageable);
        } else {
            artistPage = artistRepository.findAll(pageable);
        }

        return artistPage.map(artist -> {
            long followerCount = userArtistRepository.countByArtist(artist);
            return ArtistSummaryDto.fromEntity(artist, followerCount);
        });
    }

    // 아티스트 상세 정보 조회
    @Transactional(readOnly = true)
    public ArtistDetailDto getArtistById(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("아티스트를 찾을 수 없습니다"));
        long followerCount = userArtistRepository.countByArtist(artist);
        return ArtistDetailDto.fromEntity(artist, followerCount);
    }

    public FollowStatusResponse getFollowStatus(Long artistId, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        boolean isFollowing = userArtistRepository.findByUserIdAndArtistId(user.getId(), artistId).isPresent();
        return new FollowStatusResponse(artistId, isFollowing);
    }

    @Transactional
    public boolean toggleFollow(Long artistId, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        Artist artist = findArtistById(artistId);

        return userArtistRepository.findByUserAndArtist(user, artist)
                .map(follow -> {
                    userArtistRepository.delete(follow);
                    return false;
                })
                .orElseGet(() -> {
                    UserArtist newFollow = UserArtist.builder()
                            .user(user)
                            .artist(artist)
                            .build();
                    userArtistRepository.save(newFollow);
                    return true;
                });
    }

    @Transactional
    public Artist createNewArtistForRequest(NewArtistRequestDto dto) {
        Artist newArtist = Artist.builder()
                .artistName(dto.getName())
                .isDomestic(dto.getIsDomestic())
                .build();
        return artistRepository.save(newArtist);
    }


    private Artist findArtistById(Long artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("아티스트를 찾을 수 없습니다. ID: " + artistId));
    }

    public List<String> getArtistNamesByIds(List<Long> artistIds) {

        if (artistIds == null || artistIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Artist> artists = artistRepository.findAllById(artistIds);

        List<String> artistNames = artists.stream()
                .map(Artist::getArtistName)
                .collect(Collectors.toList());

        return artistNames;
    }

    @Transactional(readOnly = true)
    public ArtistDetailResponse getArtistDetail(Long artistId, Authentication authentication) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아티스트입니다. ID: " + artistId));

        User user = null;
        boolean isFollowing = false;

        if (authentication != null && authentication.isAuthenticated()) {
            user = authService.getUserFromAuth(authentication);
            // 1. 현재 사용자 팔로우 상태 확인 (UserArtistRepository 사용)
            isFollowing = userArtistRepository.existsByUserAndArtist(user, artist);
        }

        // 2. 팔로워 수 조회 (UserArtistRepository 사용)
        Long followerCount = userArtistRepository.countByArtist(artist);

        // 3. 관련 공연 정보 조회 (기존 로직 유지)
        List<ArtistDetailResponse.LiveInfoResponse> relatedLives = liveRepository.findLivesByArtistId(artistId)
                .stream()
                .map(live -> ArtistDetailResponse.LiveInfoResponse.builder()
                        .liveId(live.getId())
                        .title(live.getTitle())
                        .posterUrl(live.getPosterUrl())
                        .venue(live.getVenue())
                        .scheduleDates(live.getLiveSchedules().stream()
                                .map(liveSchedule -> liveSchedule.getSchedule().getLiveDate().toString())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());


        return ArtistDetailResponse.from(artist, isFollowing, followerCount, relatedLives);
    }

    @Transactional
    public boolean toggleArtistFollow(Long artistId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 커스텀 예외로 변경하는 것을 권장합니다.
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        User user = authService.getUserFromAuth(authentication);
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아티스트입니다. ID: " + artistId));

        // 💡 UserArtist 엔티티를 찾아 팔로우 상태를 확인
        Optional<UserArtist> existingFollow = userArtistRepository.findByUserAndArtist(user, artist);

        if (existingFollow.isPresent()) {
            // 언팔로우: 기존 엔티티 삭제
            userArtistRepository.delete(existingFollow.get());
            return false;
        } else {
            // 팔로우: 새 엔티티 저장
            UserArtist newFollow = UserArtist.builder()
                    .user(user)
                    .artist(artist)
                    .build();
            userArtistRepository.save(newFollow);
            return true;
        }
    }

    public void updateArtistImage(Long artistId, String imageUrl,Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아티스트입니다. ID: " + artistId));

        artistManagerRepository.findByIdUserIdAndIdArtistId(user.getId(), artistId)
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다 " + artistId));

        artist.setArtistImageUrl(imageUrl);
    }

    public void updateArtist(Long artistId, String artistName, String snsUrl, List<GenreDto> genres, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아티스트입니다. ID: " + artistId));

        artistManagerRepository.findByIdUserIdAndIdArtistId(user.getId(), artistId)
                .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다 " + artistId));

        artist.setArtistName(artistName);
        artist.setSnsUrl(snsUrl);

        if (genres != null) {
            artistGenreRepository.deleteByArtistId(artistId);

            for (GenreDto g : genres) {
                genreRepository.findById(g.getGenreId()).ifPresent(genre -> {
                    ArtistGenre artistGenre = new ArtistGenre();
                    artistGenre.setArtist(artist);
                    artistGenre.setGenre(genre);
                    artist.getArtistGenres().add(artistGenre);
                });
            }
        }

    }
}
