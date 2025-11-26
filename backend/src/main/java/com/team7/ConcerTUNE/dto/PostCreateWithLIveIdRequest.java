package com.team7.ConcerTUNE.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 게시글 작성 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateWithLIveIdRequest {

	@NotBlank(message = "제목은 필수입니다")
	private String title;

	@NotBlank(message = "내용은 필수입니다")
	private String content;

	private List<String> imageUrls;
	private List<String> fileUrls;
	private Long liveId;
}

