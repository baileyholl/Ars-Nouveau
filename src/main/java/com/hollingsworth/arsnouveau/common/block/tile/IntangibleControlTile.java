package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectIntangible;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class IntangibleControlTile extends ModdedTile implements ITickable {

    public boolean isPowered;
    public int range;


    public IntangibleControlTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.INTANGIBLE_CONTROL_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if(level.isClientSide){
            return;
        }
        range = 10;
        if(isPowered){
            Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
            // Get the 3x3 wall in front of the block
            for(int i = 1; i <= range; i++){
                BlockPos pos = worldPosition.relative(facing, i);
                // get the blocks surrounding this position, relative to the direction
                if(facing == Direction.DOWN){
                    for(int x = -1; x < 2; x++){
                        for(int z = -1; z < 2; z++){
                            BlockPos p = pos.offset(x, 0, z);
                            if(level.getBlockEntity(p) instanceof IntangibleAirTile tile) {
                                tile.duration = 0;
                            }else {
                                EffectIntangible.makeIntangible(level, p, 20);
                            }

                        }
                    }
                }
            }
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        isPowered = pTag.getBoolean("isPowered");
        range = pTag.getInt("range");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isPowered", isPowered);
        tag.putInt("range", range);
    }
}
