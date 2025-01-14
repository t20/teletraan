package com.pinterest.teletraan.universal.events;

import java.util.EventObject;
import lombok.Getter;

public abstract class AppEvent extends EventObject {
    @Getter
    private final long timestamp;

    protected AppEvent(Object source) {
        super(source);
        timestamp = System.currentTimeMillis();
    }

    protected AppEvent(Object source, long timestamp) {
        super(source);
        this.timestamp = timestamp;
    }
}
