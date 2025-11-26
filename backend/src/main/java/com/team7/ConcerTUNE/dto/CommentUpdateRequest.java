package com.team7.ConcerTUNE.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 댓글 수정 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {

	@NotBlank(message = "댓글 내용은 필수입니다")
	private String content;
}

