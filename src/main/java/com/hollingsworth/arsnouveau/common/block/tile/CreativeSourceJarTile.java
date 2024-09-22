package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CreativeSourceJarTile extends SourceJarTile {

    @Override
    public @NotNull SourceStorage getSourceStorage() {
        if (sourceStorage == null) {
            sourceStorage = new SourceStorage(getMaxSource(), getTransferRate(), getTransferRate(), getMaxSource()) {

                // Acts as void or infinite source, overrides method without doing checks or changes
                @Override
                public int receiveSource(int toReceive, boolean simulate) {
                    return toReceive;
                }

                @Override
                public int extractSource(int toExtract, boolean simulate) {
                    return toExtract;
                }
            };
            if (level != null) level.invalidateCapabilities(worldPosition);
        }
        return sourceStorage;
    }

    public CreativeSourceJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.CREATIVE_SOURCE_JAR_TILE.get(), pos, state);
    }

    @Override
    public int getSource() {
        return this.getMaxSource();
    }

    @Override
    public int getMaxSource() {
        return 1000000;
    }
}
