package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcanePedestalTile extends AnimatedTile implements Container {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    public float frames;
    public ItemEntity entity;
    private ItemStack stack = ItemStack.EMPTY;

    public ArcanePedestalTile(BlockPos pos, BlockState state){
        super(BlockRegistry.ARCANE_PEDESTAL_TILE, pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        stack = compound.contains("itemStack") ? ItemStack.of((CompoundTag)compound.get("itemStack")) : ItemStack.EMPTY;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(stack != null) {
            CompoundTag reagentTag = new CompoundTag();
            stack.save(reagentTag);
            tag.put("itemStack", reagentTag);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack == null || stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack toReturn = getItem(0).copy().split(count);
        stack.shrink(1);
        updateBlock();
        return toReturn;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return stack;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack s) {
        return stack == null || stack.isEmpty();
    }

    @Override
    public void setItem(int index, ItemStack s) {
        stack = s;
        updateBlock();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }


    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
