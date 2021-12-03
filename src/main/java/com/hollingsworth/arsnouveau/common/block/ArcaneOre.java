package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;


public class ArcaneOre extends OreBlock {
    public ArcaneOre() {
        super(TickableModBlock.defaultProperties().strength(3.0F, 3.0F));
        setRegistryName(LibBlockNames.ARCANE_ORE);
    }

    public ArcaneOre(BlockBehaviour.Properties props, UniformInt uniformInt) {
        super(props, uniformInt);
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch) {
        return super.getExpDrop(state, reader, pos, fortune, silktouch);
    }

//    @Override
//    protected int xpOnDrop(@Nonnull Random rand) {
//        return Mth.nextInt(rand, 2, 5); // same as lapis or redstone
//    }
}
