package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.common.block.tile.MirrorWeaveTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Invalidates the mirrorweave renderer after all nearby neighbors have been updated.
 */
public class InvalidateMirrorweaveRender implements ITimedEvent {

    int ticks;
    BlockPos pos;
    Level level;
    boolean expired;

    public InvalidateMirrorweaveRender(BlockPos pos, Level level) {
        this.pos = pos;
        this.level = level;
    }

    @Override
    public void tick(boolean serverSide) {
        ticks++;
        if (!expired && serverSide && ticks > 10 && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof MirrorWeaveTile tile) {
            tile.renderInvalid = true;
            tile.updateBlock();
            expired = true;
        }
    }

    @Override
    public boolean isExpired() {
        return expired;
    }
}
