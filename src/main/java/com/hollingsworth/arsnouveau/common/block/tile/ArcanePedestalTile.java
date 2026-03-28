package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArcanePedestalTile extends SingleItemTile implements Container, Nameable {
    public float frames;
    public boolean hasSignal;

    public ArcanePedestalTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ArcanePedestalTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_PEDESTAL_TILE.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putBoolean("hasSignal", hasSignal);
        if (this.name != null) {
            tag.store("CustomName", ComponentSerialization.CODEC, this.name);
        }
    }

    @Override
    protected void loadAdditional(ValueInput compound) {
        super.loadAdditional(compound);
        this.hasSignal = compound.getBooleanOr("hasSignal", false);
        this.name = compound.read("CustomName", ComponentSerialization.CODEC).orElse(null);
    }

    private Component name;

    public void setCustomName(Component pName) {
        this.name = pName;
    }

    @Override
    public @NotNull Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    protected Component getDefaultName() {
        return Component.translatable("block.ars_nouveau.arcane_pedestal");
    }
}
