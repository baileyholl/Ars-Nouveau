package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putBoolean("hasSignal", hasSignal);
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name, pRegistries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.hasSignal = compound.getBoolean("hasSignal");
        if (compound.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(compound.getString("CustomName"), pRegistries);
        }
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
