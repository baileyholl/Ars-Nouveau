package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimeline;

public interface ITimelined<T extends IParticleTimeline<T>> {
    T getTimeline();
}
