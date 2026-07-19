package com.hollingsworth.arsnouveau.api.particle.timelines;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TouchTimelinePreview extends EmitterTimelinePreview {
    private final TouchTimeline timeline;

    public TouchTimelinePreview(TouchTimeline timeline, Level level, Vec3 origin) {
        super(timeline.onResolvingEffect, level, origin.add(0, -0.5, 0));
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
