package com.hollingsworth.arsnouveau.api.scrying;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface IScryer {
    default boolean revealsBlocks() {
        return true;
    }

    default boolean shouldRevealBlock(BlockState state, BlockPos p, Player player) {
        return false;
    }

    default boolean revealsEntities() {
        return false;
    }

    default boolean shouldRevealEntity(Entity entity, Player player) {
        return false;
    }

    default ParticleColor getParticleColor() {
        return ParticleColor.DEFAULT;
    }

    IScryer fromTag(CompoundTag tag);

    default CompoundTag toTag(CompoundTag tag) {
        tag.putString("id", getRegistryName().toString());
        return tag;
    }

    ResourceLocation getRegistryName();

    Vec3i DEFAULT_SIZE = new Vec3i(20, 120, 20);

    default Vec3i getScryingSize() {
        return DEFAULT_SIZE;
    }

    default int getScryMax() {
        return 50;
    }

}
