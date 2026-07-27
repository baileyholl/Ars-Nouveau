package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.DelayTimeline;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DelayTimelinePreview extends EmitterTimelinePreview {
    private final DelayTimeline timeline;

    public DelayTimelinePreview(DelayTimeline timeline, Level level) {
        super(timeline.onTickEffect, timeline.onResolvingEffect, level, Vec3.ZERO, 40, 30f, 3, 3);
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
