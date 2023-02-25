package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.client.util.ColorPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;

public interface IWandable {
    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The FIRST IWandable in the chain is called.
     */
    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    }

    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The LAST IWandable in the chain is called.
     */
    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
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
