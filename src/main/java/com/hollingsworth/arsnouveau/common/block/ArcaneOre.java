package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.OreBlock;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nonnull;
import java.util.Random;


public class ArcaneOre extends OreBlock {
    public ArcaneOre() {
        super(ModBlock.defaultProperties());
        setRegistryName("arcane_ore");
    }

    @Override
    protected int getExperience(@Nonnull Random rand) {
        return MathHelper.nextInt(rand, 2, 5); // same as lapis or redstone
    }
}
