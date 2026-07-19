package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.PrestidigitationTimeline;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PrestidigitationTimelinePreview extends EmitterTimelinePreview {
    private final PrestidigitationTimeline timeline;

    public PrestidigitationTimelinePreview(PrestidigitationTimeline timeline, Level level) {
        super(timeline.onTickEffect, null, level, Vec3.ZERO, 40, 30f, 3, 3);
        this.timeline = timeline;
    }

    @Override
    public boolean tick(Level level) {
        if (age == 0) {
            timeline.randomSound.sound.playSound(level, position);
        }
        return super.tick(level);
    }
}
