package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CreativeSourceJarTile extends SourceJarTile {

    @Override
    protected @NotNull ISourceCap createDefaultSourceCapability() {
        return new InfiniteSourceStorage();
    }

    public CreativeSourceJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.CREATIVE_SOURCE_JAR_TILE.get(), pos, state);
    }

    public static class InfiniteSourceStorage extends SourceStorage {
        public InfiniteSourceStorage() {
            super(1000000, 1000000, 1000000, 1000000);
        }

        // Acts as void or infinite source, overrides method without doing checks or changes
        @Override
        public int receiveSource(int toReceive, boolean simulate) {
            return toReceive;
        }

        @Override
        public int extractSource(int toExtract, boolean simulate) {
            return toExtract;
        }

        @Override
        public int getSource() {
            return 1000000;
        }
    }
}
