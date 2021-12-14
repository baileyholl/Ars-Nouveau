package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.crafting.recipes.InfuserRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
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

public class CrystallizerTile extends AbstractSourceMachine implements Container, ITickable {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    public ItemStack stack = ItemStack.EMPTY;
    public ItemEntity entity;
    public boolean draining;
    InfuserRecipe recipe;
    int backoff;
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

        if(backoff > 0) {
            backoff--;
            return;
        }
        if(stack.isEmpty()) {
            draining = false;
            return;
        }

        // Restore the recipe on world restart
        if(recipe == null){
            for(InfuserRecipe recipe : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.INFUSER_TYPE)){
                if(recipe.matches(new SimpleContainer(stack), level)){
                    this.recipe = recipe;
                    break;
                }
            }
        }

        if(recipe == null || !recipe.matches(new SimpleContainer(stack), level)) {
            backoff = 20;
            recipe = null;
            return;
        }

        int transferRate = 200;

        if(recipe == null || !recipe.matches(new SimpleContainer(stack), level))
            return;

        if(this.level.getGameTime() % 20 == 0 && recipe.matches(new SimpleContainer(stack), level)){
            if(canAcceptSource(Math.min(200, recipe.source)) && SourceUtil.takeSourceNearby(worldPosition, level, 1, Math.min(200, recipe.source)) != null){
                this.addSource(transferRate);
                if(!draining) {
                    draining = true;
                    update();
                }
            }else{
                this.addSource(5);
                if(draining){
                    draining = false;
                    update();
                }
            }
        }

        if(this.getSource() >= recipe.source){
            this.setItem(0, recipe.output.copy());
            this.addSource(-recipe.source);
            draining = false;
            ParticleUtil.spawnTouchPacket(level, worldPosition, ParticleUtil.defaultParticleColorWrapper());
            update();
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
    public int getMaxSource() {
        return 5000;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if(stack.isEmpty())
            return false;
        InfuserRecipe recipe = level.getRecipeManager().getAllRecipesFor(RecipeRegistry.INFUSER_TYPE).stream()
                .filter(f -> f.matches(new SimpleContainer(stack), level)).findFirst().orElse(null);
        return recipe != null;
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
        updateBlock();
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
        updateBlock();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.stack = ItemStack.EMPTY;
        updateBlock();
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
