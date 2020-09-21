package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;


public class ArcaneOre extends ModBlock{
    public ArcaneOre() {
        super("arcane_ore");
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }
}
