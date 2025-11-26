package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 댓글 좋아요 엔티티
@Entity
@Table(name = "comment_likes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike extends BaseEntity {

    @EmbeddedId
    @Builder.Default
    private CommentLikeId id = new CommentLikeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;

    // 복합키 설정
    public void setEmbeddedId() {
        if (user != null && comment != null) {
            this.id = new CommentLikeId(user.getId(), comment.getId());
        }
    }
}
