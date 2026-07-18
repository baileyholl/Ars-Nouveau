package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.common.block.tile.MageBlockTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MageBlockTimelinePreview implements ParticleTimelinePreview {
    private final MageBlockTile tile;
    private int ticks = 0;

    public MageBlockTimelinePreview(MageBlockTimeline timeline, Level level, Vec3 origin) {
        tile = new MageBlockTile(BlockPos.ZERO, BlockRegistry.MAGE_BLOCK.get().defaultBlockState());
        tile.setColor(timeline.getColor());
    }

    @Override
    public boolean tick(Level level) {
        ticks++;
        return ticks < 40;
    }

    @Override
    public float scale() {
        return 30f;
    }

    @Override
    public void renderWorldBlocks(BlockRenderCallback callback) {
        callback.renderBlock(tile.getBlockState(), tile.getBlockPos(), tile);
    }
}
