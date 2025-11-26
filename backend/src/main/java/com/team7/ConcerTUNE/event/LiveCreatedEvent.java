package com.team7.ConcerTUNE.temp.event;

import com.team7.ConcerTUNE.entity.Artist;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LiveCreatedEvent extends ApplicationEvent {
    private final Artist artist;
    private final Long liveId;

    public LiveCreatedEvent(Object source, Artist artist, Long liveId) {
        super(source);
        this.artist = artist;
        this.liveId = liveId;
    }
}