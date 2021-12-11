package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrystallizerTile extends AbstractManaTile implements Container, ITickable {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    public ItemStack stack = ItemStack.EMPTY;
    public ItemEntity entity;
    public boolean draining;

    public CrystallizerTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.CRYSTALLIZER_TILE, pos, state);
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    @Override
    public void tick() {
        if(level.isClientSide)
            return;


        if(this.stack.isEmpty() && this.level.getGameTime() % 20 == 0 && SourceUtil.takeManaNearby(worldPosition, level, 1, 200) != null){
            this.addMana(500);
            if(!draining) {
                draining = true;
                update();
            }
        }else if(this.level.getGameTime() % 20 == 0){
            this.addMana(5);
            if(draining){
                draining = false;
                update();
            }
        }

        if(this.getCurrentMana() >= 5000 && (stack == null || stack.isEmpty())){
            Item foundItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Config.CRYSTALLIZER_ITEM.get()));
            if(foundItem == null){
                System.out.println("NULL CRYSTALLIZER ITEM.");
                foundItem = ItemsRegistry.SOURCE_GEM;
            }
            this.stack = new ItemStack(foundItem);

            this.setMana(0);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        stack = ItemStack.of((CompoundTag)tag.get("itemStack"));
        draining = tag.getBoolean("draining");
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(stack != null) {
            CompoundTag reagentTag = new CompoundTag();
            stack.save(reagentTag);
            tag.put("itemStack", reagentTag);
        }
        tag.putBoolean("draining", draining);
    }

    @Override
    public int getMaxMana() {
        return 5000;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }


    @Override
    public boolean isEmpty() {
        return this.stack == null || this.stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack copy = stack.copy();
        stack.shrink(count);
        return copy;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.stack = stack;
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
}
