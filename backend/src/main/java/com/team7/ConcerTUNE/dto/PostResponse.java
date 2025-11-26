package com.team7.ConcerTUNE.dto;

import com.team7.ConcerTUNE.entity.CommunityCategoryType;
import com.team7.ConcerTUNE.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// 게시글 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

	private Long id;
	private String title;
	private String content;
	private Long writerId;
	private String writerName;
	private CommunityCategoryType category;
	private Integer commentCount;
	private Integer viewCount;
	private Integer likeCount;
	private List<String> imageUrls;
	private List<String> fileUrls;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static PostResponse from(Post post) {
		return PostResponse.builder()
				.id(post.getId())
				.title(post.getTitle())
				.content(post.getContent())
				.writerId(post.getWriter().getId())
				.writerName(post.getWriter().getUsername())
				.category(post.getCategory())
				.commentCount(post.getCommentCount())
				.viewCount(post.getViewCount())
				.likeCount(post.getLikeCount())
				.imageUrls(post.getImageUrls())
				.fileUrls(post.getFileUrls())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build();
	}
}

