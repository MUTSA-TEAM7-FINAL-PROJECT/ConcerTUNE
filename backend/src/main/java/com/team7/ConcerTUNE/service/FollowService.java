package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.UserFollowResponse;
import com.team7.ConcerTUNE.entity.Follow;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.repository.FollowRepository;
import com.team7.ConcerTUNE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /**
     * 팔로우 / 언팔로우 토글
     */
    public void toggleFollow(User follower, Long targetId) {

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 사용자입니다."));

        if (follower.getId().equals(target.getId())) {
            throw new IllegalArgumentException("자기 자신은 팔로우 할 수 없습니다.");
        }

        // 존재하는 팔로우 관계 조회
        Follow existing = followRepository.findByFollowerAndFollowing(follower, target)
                .orElse(null);

        if (existing != null) {
            // 🔥 언팔로우 처리 - 양방향 관계 제거
            follower.getFollowings().remove(existing);
            target.getFollowers().remove(existing);

            followRepository.delete(existing);
            log.info("언팔로우 성공: {} -> {}", follower.getId(), target.getId());
        } else {
            // 🔥 팔로우 생성 및 양방향 관계 추가
            Follow follow = Follow.builder()
                    .follower(follower)
                    .following(target)
                    .build();

            follower.getFollowings().add(follow);
            target.getFollowers().add(follow);

            followRepository.save(follow);
            log.info("팔로우 성공: {} -> {}", follower.getId(), target.getId());
        }
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(User me, Long targetId) {
        return followRepository.existsByFollowerIdAndFollowingId(me.getId(), targetId);
    }

    /**
     * 나를 팔로우하는 유저 목록
     */
    @Transactional(readOnly = true)
    public Page<UserFollowResponse> getFollowers(User user, Pageable pageable) {
        Page<User> followers = followRepository.findFollowersByUser(user, pageable);

        return followers.map(follower -> UserFollowResponse.builder()
                .id(follower.getId())
                .username(follower.getUsername())
                .profileImageUrl(follower.getProfileImageUrl())
                .build());
    }

    /**
     * 내가 팔로우한 유저 목록
     */
    @Transactional(readOnly = true)
    public Page<UserFollowResponse> getFollowings(User user, Pageable pageable) {
        Page<User> followings = followRepository.findFollowingByUser(user, pageable);

        return followings.map(f -> UserFollowResponse.builder()
                .id(f.getId())
                .username(f.getUsername())
                .profileImageUrl(f.getProfileImageUrl())
                .build());
    }
}
