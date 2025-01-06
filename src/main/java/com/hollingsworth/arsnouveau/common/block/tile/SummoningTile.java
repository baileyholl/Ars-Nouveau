package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SummoningTile extends ModdedTile implements ITickable {
    public int tickCounter; // just for animation, not saved
    public boolean converted;

    public static final BooleanProperty CONVERTED = BooleanProperty.create("converted");
    public boolean isOff;

    public SummoningTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide)
            return;

        if (!converted) {
            convertedEffect();
        }
    }

    public void convertedEffect() {
        tickCounter++;
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.converted = compound.getBoolean("converted");
        this.isOff = compound.getBoolean("off");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putBoolean("converted", converted);
        tag.putBoolean("off", isOff);
    }

}
