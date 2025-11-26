package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.repository.ArtistManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistManagerService {

    private final ArtistManagerRepository artistManagerRepository;
    private final AuthService authService;
    public boolean isAdmin(Long artistId, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        return artistManagerRepository.existsByArtistIdAndUserId(artistId, user.getId());
    }
}