package com.hollingsworth.arsnouveau.api.scrying;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.ForgeRegistries;

public class SingleBlockScryer implements IScryer {
    public static SingleBlockScryer INSTANCE = new SingleBlockScryer(null);

    public SingleBlockScryer(Block block) {
        this.block = block;
    }

    public Block block;

    @Override
    public boolean shouldRevealBlock(BlockState state, BlockPos p, Player player) {
        if (block == null)
            return false;
        return state.getBlock() == block;
    }

    @Override
    public IScryer fromTag(CompoundTag tag) {
        SingleBlockScryer scryer = new SingleBlockScryer(null);
        scryer.block = tag.contains("block") ? ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("block"))) : null;
        return scryer;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (block != null)
            tag.putString("block", RegistryHelper.getRegistryName(block).toString());
        return IScryer.super.toTag(tag);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( "single_block");
    }
}
