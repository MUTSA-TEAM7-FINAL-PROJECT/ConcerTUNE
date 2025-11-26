package com.team7.ConcerTUNE.event;

import com.team7.ConcerTUNE.entity.Post;
import com.team7.ConcerTUNE.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentCreatedEvent extends ApplicationEvent {
    private final Post post;
    private final User commenter;

    public CommentCreatedEvent(Object source, Post post, User commenter) {
        super(source);
        this.post = post;
        this.commenter = commenter;
    }
}