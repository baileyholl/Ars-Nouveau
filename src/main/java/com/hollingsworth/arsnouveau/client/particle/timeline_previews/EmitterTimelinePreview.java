package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class EmitterTimelinePreview implements ParticleTimelinePreview {
    private final ParticleEmitter tickEmitter;
    private final ParticleEmitter resolveEmitter;
    protected final Vec3 position;
    private final float scale;
    private final int grassRadiusX;
    private final int grassRadiusZ;
    protected final int duration;
    protected int age;

    public EmitterTimelinePreview(TimelineEntryData effect, Level level, Vec3 position) {
        this(effect, null, level, position, 1, 30f, 3, 3);
    }

    public EmitterTimelinePreview(TimelineEntryData tickEffect, TimelineEntryData resolveEffect, Level level, Vec3 position, int duration, float scale, int grassRadiusX, int grassRadiusZ) {
        tickEmitter = new ParticleEmitter(() -> position, () -> new Vec2(0, 0), tickEffect);
        resolveEmitter = resolveEffect == null ? null : new ParticleEmitter(() -> position, () -> new Vec2(0, 0), resolveEffect);
        this.position = position;
        this.duration = duration;
        this.scale = scale;
        this.grassRadiusX = grassRadiusX;
        this.grassRadiusZ = grassRadiusZ;
    }

    @Override
    public boolean tick(Level level) {
        if (age >= duration) {
            return false;
        }
        tickEmitter.tick(level);
        age++;
        if (age == duration && resolveEmitter != null) {
            resolveEmitter.tick(level);
        }
        return true;
    }

    @Override
    public float scale() {
        return scale;
    }

    @Override
    public void renderBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, grassRadiusX, grassRadiusZ);
    }
}
