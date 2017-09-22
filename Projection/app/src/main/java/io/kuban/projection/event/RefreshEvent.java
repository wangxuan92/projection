package io.kuban.projection.event;

/**
 * Created by wangxuan on 17/6/29.
 */

public class RefreshEvent {
    public String type;

    public RefreshEvent(String type) {
        this.type = type;
    }
}
