package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.WhirlisprigTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;

public class WhirlisprigFlower extends SummonBlock{

    public WhirlisprigFlower(Properties properties, String registry) {
        super(properties, registry);
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false));
    }

    public WhirlisprigFlower(String string) {
        super(defaultProperties().noOcclusion(), string);
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WhirlisprigTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
