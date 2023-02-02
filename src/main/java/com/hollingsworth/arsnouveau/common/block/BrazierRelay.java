package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.BrazierRelayTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BrazierRelay extends TickableModBlock{
    public BrazierRelay() {
        super(defaultProperties().noOcclusion().lightLevel((b) -> b.getValue(LIT) ? 15 : 0));
    }

    public static final Property<Boolean> LIT = BooleanProperty.create("lit");

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrazierRelayTile(pos, state);
    }
}
