package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public interface IWandable {
    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The FIRST IWandable in the chain is called.
     */
    default void onFinishedConnectionFirst(@Nullable GlobalPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        ResourceKey<Level> dim;
        if (storedPos != null) {
            dim = storedPos.dimension();
        } else if (storedEntity != null) {
            dim = storedEntity.level.dimension();
        } else {
            return;
        }

        if (playerEntity.level.dimension().equals(dim)) {
            onFinishedConnectionFirst(storedPos != null ? storedPos.pos() : null, storedEntity, playerEntity);
        }
    }

    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The LAST IWandable in the chain is called.
     */
    default void onFinishedConnectionLast(@Nullable GlobalPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        ResourceKey<Level> dim;
        if (storedPos != null) {
            dim = storedPos.dimension();
        } else if (storedEntity != null) {
            dim = storedEntity.level.dimension();
        } else {
            return;
        }

        if (playerEntity.level.dimension().equals(dim)) {
            onFinishedConnectionLast(storedPos != null ? storedPos.pos() : null, storedEntity, playerEntity);
        }
    }

    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The FIRST IWandable in the chain is called.
     */
    @Deprecated
    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    }

    /**
     * When the wand has made 2 connections, block -> block, block -> entity, entity -> block, or entity -> entity.
     * The LAST IWandable in the chain is called.
     */
    @Deprecated
    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    }


    //Face-Sensitive versions

    default void onFinishedConnectionFirst(@Nullable GlobalPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        ResourceKey<Level> dim;
        if (storedPos != null) {
            dim = storedPos.dimension();
        } else if (storedEntity != null) {
            dim = storedEntity.level.dimension();
        } else {
            return;
        }

        if (playerEntity.level.dimension().equals(dim)) {
            onFinishedConnectionFirst(storedPos != null ? storedPos.pos() : null, face, storedEntity, playerEntity);
        }
    }

    default void onFinishedConnectionLast(@Nullable GlobalPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        ResourceKey<Level> dim;
        if (storedPos != null) {
            dim = storedPos.dimension();
        } else if (storedEntity != null) {
            dim = storedEntity.level.dimension();
        } else {
            return;
        }

        if (playerEntity.level.dimension().equals(dim)) {
            onFinishedConnectionLast(storedPos != null ? storedPos.pos() : null, face, storedEntity, playerEntity);
        }
    }

    @Deprecated
    default void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
    }

    @Deprecated
    default void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
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
