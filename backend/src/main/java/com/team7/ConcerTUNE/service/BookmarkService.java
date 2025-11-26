package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.entity.Bookmarks;
import com.team7.ConcerTUNE.entity.Lives;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.repository.BookmarkRepository;
import com.team7.ConcerTUNE.repository.LiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final LiveRepository liveRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public boolean isLiveBookmarked(Long liveId, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        return bookmarkRepository.existsByLiveIdAndUser(liveId, user);
    }


    @Transactional
    public boolean toggleBookmark(Long liveId, Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);
        Lives live = liveRepository.findById(liveId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘서트 ID입니다: " + liveId));

        Optional<Bookmarks> existingBookmark = bookmarkRepository.findByLiveIdAndUser(liveId, user);

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return false;
        } else {
            Bookmarks newBookmark = Bookmarks.builder()
                    .live(live)
                    .user(user)
                    .build();
            bookmarkRepository.save(newBookmark);
            return true;
        }
    }
}