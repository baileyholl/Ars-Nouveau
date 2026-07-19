package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.TouchTimeline;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TouchTimelinePreview extends EmitterTimelinePreview {
    private final TouchTimeline timeline;

    public TouchTimelinePreview(TouchTimeline timeline, Level level) {
        super(timeline.onResolvingEffect, level, Vec3.ZERO);
        this.timeline = timeline;
    }

    @Override
    public boolean tick(Level level) {
        if (age == 0) {
            timeline.resolveSound.sound.playSound(level, position);
        }
        return super.tick(level);
    }
}
