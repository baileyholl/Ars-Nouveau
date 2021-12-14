package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelayTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ArcaneRelay extends TickableModBlock {

    public ArcaneRelay() {
        this(LibBlockNames.ARCANE_RELAY);
    }

    public ArcaneRelay(String registryName){
        this(defaultProperties().lightLevel((blockState) ->8).noOcclusion(), registryName);
    }

    public ArcaneRelay(BlockBehaviour.Properties properties, String registryName){
        super(properties, registryName);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaneRelayTile(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

}
