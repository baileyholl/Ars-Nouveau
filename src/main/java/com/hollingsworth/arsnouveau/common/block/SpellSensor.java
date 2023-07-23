package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SpellSensorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SpellSensor extends TickableModBlock{

    public SpellSensor(){
        super(defaultProperties());
    }

    public SpellSensor(Properties p_49795_) {
        super(p_49795_);
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if(pBlockAccess.getBlockEntity(pPos) instanceof SpellSensorTile sensorTile){
            return sensorTile.outputStrength;
        }
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SpellSensorTile(pPos, pState);
    }
}
