package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.GenrePreferenceRequest;
import com.team7.ConcerTUNE.dto.UserResponse;
import com.team7.ConcerTUNE.dto.UserUpdateRequest;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.exception.BadRequestException;
import com.team7.ConcerTUNE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public UserResponse getMyProfile(User user) {
        User me = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return UserResponse.from(me);
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return UserResponse.from(user);
    }

    public User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Transactional
    public UserResponse updateMyProfile(User user, UserUpdateRequest request) {

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getPhoneNum() != null) {
            user.setPhoneNum(request.getPhoneNum());
        }

        if (request.getGenrePreferences() != null) {
            List<String> tags = request.getGenrePreferences().stream()
                    .map(GenrePreferenceRequest::getGenreName)
                    .toList();
            user.getTags().clear();
            user.getTags().addAll(tags);
        }

        userRepository.save(user);

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse uploadProfileImage(User user, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("파일이 비어있습니다.");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new BadRequestException("이미지 파일만 업로드 가능합니다.");
        }

        String oldUrl = user.getProfileImageUrl();

        if (oldUrl != null && isS3Url(oldUrl)) {
            fileStorageService.deleteFile(oldUrl);
        }

        String newUrl = fileStorageService.uploadFile(file, "profiles");
        user.setProfileImageUrl(newUrl);
        userRepository.save(user);

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse deleteProfileImage(User user) {
        String oldUrl = user.getProfileImageUrl();

        if (oldUrl == null || oldUrl.isBlank()) {
            throw new IllegalStateException("삭제할 프로필 이미지가 없습니다.");
        }

        if (isS3Url(oldUrl)) {
            fileStorageService.deleteFile(oldUrl);
        }

        user.setProfileImageUrl(null);
        userRepository.save(user);

        return UserResponse.from(user);
    }

    private boolean isS3Url(String url) {
        return url != null && url.contains(bucketName);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
