package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

public class CrystallizerTile extends AbstractManaTile implements IInventory {
    public ItemStack stack = ItemStack.EMPTY;
    public ItemEntity entity;
    public boolean draining;

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


        if(this.stack.isEmpty() && this.world.getGameTime() % 20 == 0 && ManaUtil.takeManaNearby(pos, world, 1, 500) != null){
            this.addMana(500);
            if(!draining) {
                draining = true;
                update();
            }
        }else if(this.world.getGameTime() % 20 == 0){
            this.addMana(15);
            if(draining){
                draining = false;
                update();
            }
        }

        if(this.getCurrentMana() >= 5000 && (stack == null || stack.isEmpty())){
            this.stack = new ItemStack(BlockRegistry.MANA_GEM_BLOCK);
            this.setMana(0);
        }


    }

    @Override
    public void read(CompoundNBT tag) {
        stack = ItemStack.read((CompoundNBT)tag.get("itemStack"));
        draining = tag.getBoolean("draining");
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if(stack != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            stack.write(reagentTag);
            tag.put("itemStack", reagentTag);
        }
        tag.putBoolean("draining", draining);
        return super.write(tag);
    }

    @Override
    public int getMaxMana() {
        return 5000;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }


    @Override
    public boolean isEmpty() {
        return this.stack == null || this.stack.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack copy = stack.copy();
        stack.shrink(count);
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
