package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import java.util.Random;


public class ArcaneOre extends OreBlock {
    public ArcaneOre() {
        super(ModBlock.defaultProperties().strength(3.0F, 3.0F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
        setRegistryName(LibBlockNames.ARCANE_ORE);
    }

    @Override
    protected int xpOnDrop(@Nonnull Random rand) {
        return Mth.nextInt(rand, 2, 5); // same as lapis or redstone
    }
}
