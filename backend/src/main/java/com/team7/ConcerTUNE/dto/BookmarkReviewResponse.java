package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Live;
import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkReviewResponse {

    private Long postId;

    private Long liveId;
    private String liveTitle;
    private String livePosterUrl;

    private String postTitle;
    private String content;

    private Long writerId;
    private String writerName;   // 닉네임 or username

    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;

    public static BookmarkReviewResponse fromEntity(Post post) {
        Live live = post.getLive();
        User writer = post.getWriter();

        return BookmarkReviewResponse.builder()
                .postId(post.getId())
                .liveId(live != null ? live.getId() : null)
                .liveTitle(live != null ? live.getTitle() : null)
                .livePosterUrl(live != null ? live.getPosterUrl() : null)
                .postTitle(post.getTitle())
                .content(post.getContent())
                .writerId(writer.getId())
                .writerName(writer.getUsername())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
