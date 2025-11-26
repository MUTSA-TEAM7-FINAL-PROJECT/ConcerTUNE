package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 댓글 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

	private Long id;
	private String content;
	private Long writerId;
	private String writerName;
	private Long postId;
	private Integer likeCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long parentCommentId;
	@Builder.Default
	private List<CommentResponse> replies = new ArrayList<>();

	public static CommentResponse from(Comment comment) {
		return CommentResponse.from(comment, true);
	}

	private static CommentResponse from(Comment comment, boolean includeReplies) {
		CommentResponseBuilder builder = CommentResponse.builder()
				.id(comment.getId())
				.content(comment.getContent())
				.writerId(comment.getWriter().getId())
				.writerName(comment.getWriter().getUsername())
				.postId(comment.getPost().getId())
				.likeCount(comment.getLikeCount())
				.createdAt(comment.getCreatedAt())
				.updatedAt(comment.getUpdatedAt())
				.parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);

		if (includeReplies) {
			List<CommentResponse> childResponses = comment.getReplies().stream()
					.map(child -> CommentResponse.from(child, true))
					.collect(Collectors.toList());
			builder.replies(childResponses);
		}

		return builder.build();
	}
}

