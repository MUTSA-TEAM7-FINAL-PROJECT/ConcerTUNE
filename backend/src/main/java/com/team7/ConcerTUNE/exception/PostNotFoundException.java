package com.team7.ConcerTUNE.exception;

// 게시글을 찾을 수 없을 때 발생하는 예외
public class PostNotFoundException extends RuntimeException {
	public PostNotFoundException(Long postId) {
		super("게시글을 찾을 수 없습니다: " + postId);
	}
}

