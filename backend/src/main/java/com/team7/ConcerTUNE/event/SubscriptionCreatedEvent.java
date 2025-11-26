package com.team7.ConcerTUNE.event;

import com.team7.ConcerTUNE.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SubscriptionCreatedEvent extends ApplicationEvent {
    private final User user;

    public SubscriptionCreatedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}