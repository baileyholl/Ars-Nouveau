package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.util.registry.RegistryWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SingleItemTile extends ModdedTile implements Container{
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    protected ItemStack stack = ItemStack.EMPTY;
    public ItemEntity renderEntity;

    public SingleItemTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public SingleItemTile(RegistryWrapper<? extends BlockEntityType<?>> tileEntityTypeIn, BlockPos pos, BlockState state) {
        this(tileEntityTypeIn.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack copyStack = stack.copy().split(pAmount);
        stack.shrink(pAmount);
        updateBlock();
        return copyStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        updateBlock();
        return stack;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        this.stack = pStack;
        updateBlock();
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return stack.isEmpty();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
        updateBlock();
    }

    public ItemStack getStack(){
        return this.stack;
    }

    public void setStack(ItemStack otherStack){
        this.stack = otherStack;
        updateBlock();
    }

   @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, final @Nullable Direction side) {
        if (cap == Capabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        stack = ItemStack.of((CompoundTag) compound.get("itemStack"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (stack != null) {
            CompoundTag stackTag = new CompoundTag();
            stack.save(stackTag);
            tag.put("itemStack", stackTag);
        }
    }
}
