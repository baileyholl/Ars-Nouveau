package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public final class WallTimelinePreview implements ParticleTimelinePreview {
    private final ParticleEmitter tickEmitter;
    private final ParticleEmitter resolveEmitter;
    private int age;

    public WallTimelinePreview(WallTimeline timeline, Vec3 origin) {
        Vec3 position = origin.add(0, -1, 0);
        tickEmitter = new ParticleEmitter(() -> position, () -> new Vec2(0, 0), timeline.trailEffect);
        resolveEmitter = new ParticleEmitter(() -> position, () -> new Vec2(0, 0), timeline.onResolvingEffect);
    }

    @Override
    public boolean tick(Level level) {
        if (age >= 40) {
            return false;
        }
        tickEmitter.tick(level);
        age++;
        if (age == 40) {
            resolveEmitter.tick(level);
        }
        return true;
    }

    @Override
    public float scale() {
        return 10f;
    }

    @Override
    public void renderWorldBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 1, 5);
        for (int y = -1; y <= 4; y++) {
            for (int z = -5; z <= 5; z++) {
                callback.renderBlock(Blocks.STONE_BRICKS.defaultBlockState(), new BlockPos(1, y, z));
            }
        }
    }
}
