package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.util.registry.RegistryWrapper;
import net.minecraft.core.BlockPos;
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

    public SummoningTile(RegistryWrapper<? extends BlockEntityType<?>> type, BlockPos pos, BlockState state) {
        this(type.get(), pos, state);
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
    public void load(CompoundTag compound) {
        super.load(compound);
        this.converted = compound.getBoolean("converted");
        this.isOff = compound.getBoolean("off");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putBoolean("converted", converted);
        tag.putBoolean("off", isOff);
    }

}
