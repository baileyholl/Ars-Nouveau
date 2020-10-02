package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public class CrystallizerTile extends AbstractManaTile implements IInventory {
    public ItemStack stack = ItemStack.EMPTY;
    public CrystallizerTile() {
        super(BlockRegistry.CRYSTALLIZER_TILE);
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;

        if(this.stack == ItemStack.EMPTY)
            this.stack = new ItemStack(ItemsRegistry.manaGem);
        if(this.getCurrentMana() == 2500 && (stack == ItemStack.EMPTY || stack == null)){
            this.stack = new ItemStack(ItemsRegistry.manaGem);
            update();
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        stack = ItemStack.read((CompoundNBT)tag.get("itemStack"));
        super.read(tag);

    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if(stack != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            stack.write(reagentTag);
            tag.put("itemStack", reagentTag);
        }
        return super.write(tag);
    }

    @Override
    public int getMaxMana() {
        return 2500;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.stack == ItemStack.EMPTY || this.stack == null;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack copy = stack.copy();
        stack.shrink(count);
        System.out.println("returning" + copy);
        return copy;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stack = ItemStack.EMPTY;
    }
}
