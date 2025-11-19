package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.entity.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostListResponse {

    private Long id;
    private CommunityCategoryType category;
    private String title;
    private String writerUsername;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private List<String> images;
    private List<String> files;
    private LocalDateTime createdAt;

    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .writerUsername(post.getWriter().getUsername())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .images(post.getImageUrls())
                .files(post.getFileUrls())
                .build();
    }

    public static Page<PostListResponse> fromPage(Page<Post> postPage) {
        return postPage.map(PostListResponse::from);
    }
}