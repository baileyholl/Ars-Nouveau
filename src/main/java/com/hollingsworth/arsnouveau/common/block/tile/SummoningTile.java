package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SummoningTile extends ModdedTile implements ITickable {
    public int tickCounter; // just for animation, not saved
    public boolean converted;

    public static final BooleanProperty CONVERTED = BooleanProperty.create("converted");

    public SummoningTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        if(level.isClientSide)
            return;

        if (!converted) {
            convertedEffect();
        }
    }

    public void convertedEffect() {
        tickCounter++;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.converted = compound.getBoolean("converted");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putBoolean("converted", converted);
    }

}
