package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
        if (level.isClientSide())
            return;

        if (!converted) {
            convertedEffect();
        }
    }

    public void convertedEffect() {
        tickCounter++;
    }

    @Override
    protected void loadAdditional(ValueInput compound) {
        super.loadAdditional(compound);
        this.converted = compound.getBooleanOr("converted", false);
        this.isOff = compound.getBooleanOr("off", false);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putBoolean("converted", converted);
        tag.putBoolean("off", isOff);
    }

}
