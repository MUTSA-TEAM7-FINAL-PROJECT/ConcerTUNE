package com.team7.ConcerTUNE.temp.event;

import com.team7.ConcerTUNE.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FollowEvent extends ApplicationEvent {
    private final User follower;
    private final User target;

    public FollowEvent(Object source, User follower, User target) {
        super(source);
        this.follower = follower;
        this.target = target;
    }
}