package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;

public class ArcaneOre extends ModBlock{
    public ArcaneOre() {
        super("arcane_ore");
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

}
