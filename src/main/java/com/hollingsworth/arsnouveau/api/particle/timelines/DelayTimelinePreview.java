package com.hollingsworth.arsnouveau.api.particle.timelines;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DelayTimelinePreview extends EmitterTimelinePreview {
    private final DelayTimeline timeline;

    public DelayTimelinePreview(DelayTimeline timeline, Level level, Vec3 origin) {
        super(timeline.onTickEffect, timeline.onResolvingEffect, level, origin.add(0, -0.5, 0), 40, 30f, 3, 3);
        this.timeline = timeline;
    }

    @Override
    public boolean tick(Level level) {
        boolean active = super.tick(level);
        if (active && age == duration) {
            timeline.resolvingSound.sound.playSound(level, position);
        }
        return active;
    }
}
