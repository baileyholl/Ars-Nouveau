package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ArcaneCore extends ModBlock implements EntityBlock {
    public ArcaneCore() {
        super(defaultProperties().noOcclusion().lightLevel((state) -> 15), LibBlockNames.ARCANE_CORE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneCoreTile(pos, state);
    }


    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

}
