package com.hollingsworth.arsnouveau.client.particle.timeline_previews;

import com.hollingsworth.arsnouveau.api.particle.timelines.MageBlockTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimelinePreview;
import com.hollingsworth.arsnouveau.common.block.tile.MageBlockTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class MageBlockTimelinePreview implements ParticleTimelinePreview {
    private final MageBlockTile tile;
    private int ticks = 0;

    public MageBlockTimelinePreview(MageBlockTimeline timeline, Level level) {
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
    public void renderBlocks(BlockRenderCallback callback) {
        callback.renderBlock(tile.getBlockState(), tile.getBlockPos(), tile);
    }
}
