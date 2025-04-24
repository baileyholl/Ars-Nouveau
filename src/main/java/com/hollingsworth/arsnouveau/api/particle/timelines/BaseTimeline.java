package com.hollingsworth.arsnouveau.api.particle.timelines;

import java.util.List;
import java.util.Objects;

public abstract class BaseTimeline<T extends IParticleTimeline<T>> implements IParticleTimeline<T>{

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseTimeline that = (BaseTimeline) o;
        List<TimelineOption> thisTimelineOptions = this.getTimelineOptions();
        List<TimelineOption> thatTimelineOptions = that.getTimelineOptions();
        return thisTimelineOptions.equals(thatTimelineOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimelineOptions());
    }

}
