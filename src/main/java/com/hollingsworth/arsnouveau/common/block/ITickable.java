package com.hollingsworth.arsnouveau.common.block;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for tickable things.
 */
public interface ITickable {
    /**
     * Tick the tickable with parameters.
     *
     * @param level the world its ticking in.
     * @param state its state.
     * @param pos   the position its ticking at.
     */
    default void tick(final Level level, final BlockState state, final BlockPos pos) {
        tick();
    }

    /**
     * Default parameterless ticking implementation.
     * WARNING:
     * THIS METHOD WON'T WORK IF {@link ITickable#tick(Level, BlockState, BlockPos)} IS OVERRIDDEN IN THE SUPERCLASS WITHOUT THE SUPER CALL
     */
    default void tick() {
    }

}
