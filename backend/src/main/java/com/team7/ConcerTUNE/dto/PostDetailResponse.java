package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailResponse {
    // Post API: GET /api/posts/{category}/{postId}

    private Long id;
    private CommunityCategoryType category;
    private String title;
    private String content;
    private String writerUsername;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private List<String> imageUrls;
    private List<String> fileUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long writerId;

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .writerUsername(post.getWriter().getUsername())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .imageUrls(post.getImageUrls())
                .fileUrls(post.getFileUrls())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .writerId(post.getWriter().getId())
                .build();
    }
}