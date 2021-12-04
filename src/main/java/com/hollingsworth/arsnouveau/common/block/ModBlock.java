package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public class ModBlock extends Block {

    public ModBlock(Properties properties, String registry) {
        super(properties);
        setRegistryName(registry);
    }

    public ModBlock(String registryName){
        this(defaultProperties(), registryName);
    }

    public ModBlock(Properties properties){
        super(properties);
    }

    public static Block.Properties defaultProperties(){
        return Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 6.0f);
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
        return true;
    }
}
