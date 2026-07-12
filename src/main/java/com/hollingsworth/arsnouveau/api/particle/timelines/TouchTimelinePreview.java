package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public final class TouchTimelinePreview implements ParticleTimelinePreview {
    private final ParticleEmitter emitter;
    private boolean played;

    public TouchTimelinePreview(TouchTimeline timeline, Vec3 origin) {
        Vec3 position = origin.add(0, -0.5, 0);
        emitter = new ParticleEmitter(() -> position, () -> new Vec2(0, 0), timeline.onResolvingEffect);
    }

    @Override
    public boolean tick(Level level) {
        if (played) {
            return false;
        }
        emitter.tick(level);
        played = true;
        return true;
    }

    @Override
    public Scene scene() {
        return new Scene(30f, 1, 1);
    }
}
