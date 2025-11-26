package com.team7.ConcerTUNE.temp.dto;

import com.team7.ConcerTUNE.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostSummaryDto {

    private Long postId;
    private String category;
    private String title;
    private String writer;
    private int likeCount;
    private int commentCount;
    private List<String> imageUrls;

    public PostSummaryDto(Post post) {
        this.postId = post.getId();
        this.category = post.getCategory().name().toLowerCase();
        this.title = post.getTitle();

        this.writer = post.getWriter().getUsername();

        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();

        if (post.getImageUrls() != null) {
            this.imageUrls = new ArrayList<>(post.getImageUrls());
        } else {
            this.imageUrls = new ArrayList<>();
        }
    }
}