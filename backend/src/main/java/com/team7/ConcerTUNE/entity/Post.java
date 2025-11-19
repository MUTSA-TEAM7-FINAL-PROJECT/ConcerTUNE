package com.team7.ConcerTUNE.entity;

import com.team7.ConcerTUNE.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "post_title", length = 200)
    private String title;

    @Column(name = "post_content", length = 65536)
    private String content;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "post_like_count")
    private Integer likeCount = 0;

    @Convert(converter = StringListConverter.class)
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private List<String> imageUrls = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(name = "file_urls", columnDefinition = "TEXT")
    private List<String> fileUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_writer_id", nullable = false)
    private User writer;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_id", length = 48, nullable = false)
    private CommunityCategoryType category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_id")
    private Lives live;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    // Service에서 사용하기 위해 Post 생성 메서드 수정: Live 객체 추가
    public static Post create(CommunityCategoryType category, String title, String content,
                              List<String> imageUrls, List<String> fileUrls, User writer, Lives live) {
        Post post = new Post();
        post.category = category;
        post.title = title;
        post.content = content;
        post.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
        post.fileUrls = fileUrls != null ? new ArrayList<>(fileUrls) : new ArrayList<>();
        post.writer = writer;
        post.live = live; // Live 엔티티 할당
        return post;
    }

    // Service에서 사용하기 위해 Post 수정 메서드 추가
    public void update(String title, String content, List<String> imageUrls, List<String> fileUrls) {
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
        this.fileUrls = fileUrls != null ? new ArrayList<>(fileUrls) : new ArrayList<>();
        // Live 정보는 일반적으로 수정되지 않으므로 update 메서드에서 제외했습니다.
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        this.commentCount--;
    }

}