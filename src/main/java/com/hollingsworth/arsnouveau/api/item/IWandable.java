package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IWandable {
    /**
     * Deprecated, use the version that support link removal. see {@link #onFinishedConnectionFirst(BlockPos, Direction, LivingEntity, Player, boolean)}
     *
     * <p>
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The FIRST IWandable in the chain is called.
     */
    @Deprecated(forRemoval = true)
    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    }

    /**
     * Deprecated, use the version that support link removal. see {@link #onFinishedConnectionLast(BlockPos, Direction, LivingEntity, Player, boolean)}
     * <p>
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The LAST IWandable in the chain is called.
     */
    @Deprecated(forRemoval = true)
    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    }

    //Face-Sensitive versions

    /**
     * Deprecated, use the version that support link removal. see {@link #onFinishedConnectionFirst(BlockPos, Direction, LivingEntity, Player, boolean)}
     *
     * <p>
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The FIRST IWandable in the chain is called.
     */
    @Deprecated(forRemoval = true)
    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
    }

    /**
     * Deprecated, use the version that support link removal. see {@link #onFinishedConnectionFirst(BlockPos, Direction, LivingEntity, Player, boolean)}
     *
     * <p>
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The LAST IWandable in the chain is called.
     */
    @Deprecated(forRemoval = true)
    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
    }

    //Face-Sensitive versions
    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity, boolean remove) {
        onFinishedConnectionFirst(storedPos, storedEntity, playerEntity, remove);
    }

    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity, boolean remove) {
        onFinishedConnectionLast(storedPos, storedEntity, playerEntity, remove);
    }

    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity, boolean remove) {
        onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
    }

    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The LAST IWandable in the chain is called.
     */
    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity, boolean remove) {
        onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
    }

    /**
     * Called on the time of wanding.
     */
    default void onWanded(Player playerEntity) {
    }

    /**
     * Return the list of positions to highlight when the wand is held and this entity or block is selected.
     */
    default List<ColorPos> getWandHighlight(List<ColorPos> list) {
        return list;
    }
}
