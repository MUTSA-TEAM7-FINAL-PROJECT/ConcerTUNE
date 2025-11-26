package com.team7.ConcerTUNE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시글 좋아요 엔티티
@Entity
@Table(name = "post_likes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLike extends BaseEntity {

    @EmbeddedId
    @Builder.Default
    private PostLikeId id = new PostLikeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    // 복합키 설정
    public void setEmbeddedId() {
        if (user != null && post != null) {
            this.id = new PostLikeId(user.getId(), post.getId());
        }
    }
}