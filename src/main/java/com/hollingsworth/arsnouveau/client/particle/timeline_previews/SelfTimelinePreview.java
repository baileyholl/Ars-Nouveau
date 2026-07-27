package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.SelfTimeline;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SelfTimelinePreview extends EmitterTimelinePreview {
    private final SelfTimeline timeline;

    public SelfTimelinePreview(SelfTimeline timeline, Level level) {
        super(timeline.onResolvingEffect, level, new Vec3(0, -0.5, 0));
        this.timeline = timeline;
    }

    @Override
    public boolean tick(Level level) {
        if (age == 0) {
            timeline.resolveSound.sound.playSound(level, Vec3.ZERO);
        }
        return super.tick(level);
    }
}
