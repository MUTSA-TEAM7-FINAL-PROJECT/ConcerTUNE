package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private String writerUsername;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long writerId;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writerUsername(comment.getWriter().getUsername())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .writerId(comment.getWriter().getId())
                .build();
    }

    public static List<CommentResponse> fromList(List<Comment> comments) {
        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }
}