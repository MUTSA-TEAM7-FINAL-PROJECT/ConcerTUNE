package com.team7.ConcerTUNE.exception;

// 댓글을 찾을 수 없을 때 발생하는 예외
public class CommentNotFoundException extends RuntimeException {
	public CommentNotFoundException(Long commentId) {
		super("댓글을 찾을 수 없습니다: " + commentId);
	}
}

