package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class BurstTimelinePreview implements ParticleTimelinePreview {
    private final List<ParticleEmitter> emitters = new ArrayList<>();
    private boolean emitted;

    public BurstTimelinePreview(BurstTimeline timeline, Level level, Vec3 origin) {
        int radius = 3;
        double maxDistanceSqr = (radius + 0.5) * (radius + 0.5);
        Vec3 center = origin.add(0, 2, 0);
        for (BlockPos offset : BlockPos.withinManhattan(BlockPos.ZERO, radius, radius, radius)) {
            if (offset.getX() * offset.getX() + offset.getY() * offset.getY() + offset.getZ() * offset.getZ() <= maxDistanceSqr) {
                Vec3 position = center.add(offset.getX(), offset.getY(), offset.getZ());
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
        emitted = true;
        return true;
    }

    @Override
    public float scale() {
        return 10f;
    }

    @Override
    public void renderWorldBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 10, 10);
    }
}
