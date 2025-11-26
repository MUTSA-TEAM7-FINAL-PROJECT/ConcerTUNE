package com.team7.ConcerTUNE.temp.event;

import com.team7.ConcerTUNE.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ArtistManagerRequestEvent extends ApplicationEvent {
    private final User user;
    private final boolean approved;

    public ArtistManagerRequestEvent(Object source, User user, boolean approved) {
        super(source);
        this.user = user;
        this.approved = approved;
    }
}