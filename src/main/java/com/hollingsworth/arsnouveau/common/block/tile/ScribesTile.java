package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScribesTile extends ModdedTile implements IAnimatable, ITickable, Container {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    public ItemEntity entity; // For rendering
    public ItemStack stack = ItemStack.EMPTY;
    public int frames;

    List<ItemStack> consumedStacks = new ArrayList<>();
    GlyphRecipe recipe;


    public ScribesTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCRIBES_TABLE_TILE, pos, state);
    }

    @Override
    public void tick() {
        if(recipe != null){
            System.out.println(recipe.inputs);
        }
    }

    public void setRecipe(GlyphRecipe recipe){
        this.recipe = recipe;
        updateBlock();
        System.out.println("seetRecipe");
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        stack = ItemStack.of((CompoundTag)compound.get("itemStack"));
        if(compound.contains("recipe")){
            recipe = (GlyphRecipe) level.getRecipeManager().byKey(new ResourceLocation(compound.getString("recipe"))).orElse(null);
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if(stack != null) {
            CompoundTag reagentTag = new CompoundTag();
            stack.save(reagentTag);
            compound.put("itemStack", reagentTag);
        }
        if(recipe !=  null){
            compound.putString("recipe", recipe.getId().toString());
        }
    }

    @Override
    public void registerControllers(AnimationData data) {}

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(2);
    }

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack == null || this.stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        ItemStack copy = stack.copy();
        stack.shrink(1);
        updateBlock();
        return copy;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        ItemStack stack = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        updateBlock();
        return stack;
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.stack = pStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
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
