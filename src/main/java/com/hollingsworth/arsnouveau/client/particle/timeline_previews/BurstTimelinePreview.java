package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.BurstTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class BurstTimelinePreview implements ParticleTimelinePreview {
    private final List<ParticleEmitter> emitters = new ArrayList<>();
    private final BurstTimeline timeline;
    private boolean emitted;

    public BurstTimelinePreview(BurstTimeline timeline, Level level) {
        this.timeline = timeline;
        int radius = 3;
        double maxDistanceSqr = (radius + 0.5) * (radius + 0.5);
        for (BlockPos offset : BlockPos.withinManhattan(BlockPos.ZERO, radius, radius, radius)) {
            if (offset.getX() * offset.getX() + offset.getY() * offset.getY() + offset.getZ() * offset.getZ() <= maxDistanceSqr) {
                Vec3 position = new Vec3(offset.getX(), offset.getY() + 2, offset.getZ());
                emitters.add(new ParticleEmitter(() -> position, () -> new Vec2(0, 0), timeline.onResolvingEffect));
            }
        }
    }

    @Override
    public boolean tick(Level level) {
        if (emitted) {
            return false;
        }
        for (ParticleEmitter emitter : emitters) {
            emitter.tick(level);
        }
        timeline.resolveSound.sound.playSound(level, Vec3.ZERO);
        emitted = true;
        return true;
    }

    @Override
    public float scale() {
        return 10f;
    }

    @Override
    public void renderBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 10, 10);
    }
}
