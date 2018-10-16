package io.kuban.projection.event;

/**
 * Created by wangxuan on 17/11/21.
 */

public class BindingResultsEvent {
    private boolean results;

    public boolean isResults() {
        return results;
    }

    public BindingResultsEvent(boolean results) {
        this.results = results;
    }
}
