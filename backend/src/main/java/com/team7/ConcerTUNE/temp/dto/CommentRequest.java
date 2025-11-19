package com.team7.ConcerTUNE.temp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {
    // Comment API: POST /api/posts/{postId}/comments & PUT /api/comments/{commentId}

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}