package com.team7.ConcerTUNE.temp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookmarkedPostDto {

    private Long postId;
    private String title;
    private String writer;
    private Long likeCount;
    private String liveName;

    public BookmarkedPostDto(Long postId, String title, String writerName, Long likeCount, String liveName) {
        this.postId = postId;
        this.title = title;
        this.writer = writerName;
        this.likeCount = likeCount;
        this.liveName = liveName;
    }
}