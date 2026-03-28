package com.hollingsworth.arsnouveau.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;

public class MagicLeaves extends LeavesBlock {
    public static final MapCodec<MagicLeaves> CODEC = simpleCodec(MagicLeaves::new);

    public MagicLeaves(Properties properties) {
        super(0.2f, properties);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        // no falling leaves particle
    }
}
