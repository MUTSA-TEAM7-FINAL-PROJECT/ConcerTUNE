package com.team7.ConcerTUNE.event;

import com.team7.ConcerTUNE.entity.LiveRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LiveRequestEvent extends ApplicationEvent {
    private final LiveRequest liveRequest;
    private final boolean approved;

    public LiveRequestEvent(Object source, LiveRequest liveRequest, boolean approved) {
        super(source);
        this.liveRequest = liveRequest;
        this.approved = approved;
    }
}