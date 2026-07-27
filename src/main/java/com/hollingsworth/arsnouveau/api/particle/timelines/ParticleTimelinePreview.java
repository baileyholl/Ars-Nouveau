package com.hollingsworth.arsnouveau.api.particle.timelines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ParticleTimelinePreview {

    boolean tick(Level level);

    default float scale() {
        return 15f;
    }

    default void renderBlocks(BlockRenderCallback callback) {
        renderGrassField(callback);
    }

    default void renderEntities(EntityRenderCallback callback) {
    }

    interface EntityRenderCallback {
        void renderEntity(Entity entity);
    }

    interface BlockRenderCallback {
        void renderBlock(BlockState state, BlockPos pos);

        default void renderBlock(BlockState state, BlockPos pos, BlockEntity blockEntity) {
            renderBlock(state, pos);
        }

        void renderBlockEntity(BlockEntity blockEntity);
    }

    static void renderGrassField(BlockRenderCallback callback, int radiusX, int radiusZ) {
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int z = -radiusZ; z <= radiusZ; z++) {
                callback.renderBlock(Blocks.GRASS_BLOCK.defaultBlockState(), new BlockPos(x, -1, z));
            }
        }
    }

    static void renderGrassField(BlockRenderCallback callback) {
        renderGrassField(callback, 3, 2);
    }
}
