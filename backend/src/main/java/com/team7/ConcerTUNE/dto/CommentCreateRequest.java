package com.team7.ConcerTUNE.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 댓글 작성 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

	@NotBlank(message = "댓글 내용은 필수입니다")
	private String content;

	// null이면 최상위 댓글, 값을 넣으면 해당 댓글의 자식으로 저장
	private Long parentCommentId;
}

