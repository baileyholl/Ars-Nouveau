package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;

import java.util.List;
import java.util.Objects;

public abstract class BaseTimeline<T extends IParticleTimeline<T>> implements IParticleTimeline<T> {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseTimeline that = (BaseTimeline) o;
        List<BaseProperty<?>> thisTimelineOptions = this.getProperties();
        List<BaseProperty<?>> thatTimelineOptions = that.getProperties();

        return thisTimelineOptions.equals(thatTimelineOptions) && this.getProperties().equals(that.getProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProperties(), ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY.getKey(this.getType()));
    }

}
