package com.hollingsworth.arsnouveau.common.block.tile;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlyphPressTile extends AnimatedTile implements ITickableTileEntity, IAnimatable, IAnimationListener, ISidedInventory {
    private final Map<Direction, LazyOptional<IItemHandler>> itemHandlers = new HashMap<>();
    public long frames = 0;
    public boolean isCrafting;
    public ItemStack reagentItem = ItemStack.EMPTY;
    public ItemStack baseMaterial = ItemStack.EMPTY;
    public ItemStack oldBaseMat = ItemStack.EMPTY;
    public ItemEntity entity;

    public GlyphPressTile() {
        super(BlockRegistry.GLYPH_PRESS_TILE);
        ImmutableList.of(Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST).forEach(this::addItemHandler);
        addItemHandler(null);
    }

    @Override
    public void tick() {
        if(!level.isClientSide && level.getGameTime() % 20 == 0 && canCraft(reagentItem, baseMaterial))
            craft(FakePlayerFactory.getMinecraft((ServerWorld) level));

        if(level.isClientSide || !isCrafting){
            return;
        }
        counter += 1;
        if(counter == 110){
            GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(level, reagentItem.getItem(), getTier(baseMaterial.getItem()));
            oldBaseMat = this.baseMaterial.copy();
            this.baseMaterial = recipe.output.copy();
            updateBlock();
        }
        if(counter ==150) {
            isCrafting = false;
            GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(level, reagentItem.getItem(), getTier(oldBaseMat.getItem()));
            if(recipe == null)
                return;

            ItemStack stack = recipe.output.copy();
            if(!stack.isEmpty())
                level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY()+ 1.5, worldPosition.getZ()+0.5, stack));
            reagentItem = ItemStack.EMPTY;
            this.baseMaterial = ItemStack.EMPTY;
            this.oldBaseMat = ItemStack.EMPTY;
            counter = 1;

        }
        updateBlock();
    }


    public void updateBlock(){
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 2);
    }


    public boolean craft(PlayerEntity playerEntity) {
        if(isCrafting || baseMaterial == ItemStack.EMPTY)
            return false;
        GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(level, reagentItem.getItem(), getTier(this.baseMaterial.getItem()));
        if(recipe == null)
            return false;

        int manaCost = recipe.tier == ISpellTier.Tier.ONE ? 1000 : (recipe.tier == ISpellTier.Tier.TWO ? 2000 : 3000);
        BlockPos jar = ManaUtil.takeManaNearbyWithParticles(worldPosition, level, 5, manaCost);
        if(jar != null){
            isCrafting = true;
            Networking.sendToNearby(level, worldPosition, new PacketOneShotAnimation(worldPosition));
            return true;
        }

        playerEntity.sendMessage(new TranslationTextComponent("ars_nouveau.glyph_press.no_mana"), Util.NIL_UUID);
        return false;
    }


    public ISpellTier.Tier getTier(Item clay){
        if(clay == ItemsRegistry.magicClay)
            return ISpellTier.Tier.ONE;
        else if(clay == ItemsRegistry.marvelousClay){
            return ISpellTier.Tier.TWO;
        }else if(clay == ItemsRegistry.mythicalClay)
            return ISpellTier.Tier.THREE;
        return null;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 1, this::idlePredicate));
    }
    AnimationFactory manager = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    private <E extends TileEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void startAnimation(int arg) {
        AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("controller");
        controller.markNeedsReload();
        controller.setAnimation(new AnimationBuilder().addAnimation("press", false));
    }
    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return reagentItem.isEmpty() && baseMaterial.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? reagentItem : index == 1 ? baseMaterial : ItemStack.EMPTY ;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if(index == 0){
            ItemStack stack = reagentItem.copy();
            stack.setCount(count);
            reagentItem.shrink(count);
            updateBlock();
            return stack;
        }else if(index == 1){
            ItemStack stack = baseMaterial.copy();
            stack.shrink(count);
            baseMaterial.shrink(count);
            updateBlock();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = reagentItem.copy();
        reagentItem.setCount(0);
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if(index == 0)
            reagentItem = stack;
        if(index == 1)
            baseMaterial = stack;

        updateBlock();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void clearContent() {
        reagentItem = ItemStack.EMPTY;
        baseMaterial = ItemStack.EMPTY;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.UP ? new int[]{0} : side != Direction.DOWN ? new int[]{1} : new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        if(isCrafting)
            return false;
        if(index == 0 && reagentItem.isEmpty() && direction == Direction.UP){
            return canCraft(itemStackIn, baseMaterial);
        }else if(index == 1 && direction != Direction.UP && direction != Direction.DOWN && baseMaterial.isEmpty()){
            return getTier(itemStackIn.getItem()) != null;
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    public boolean canCraft(ItemStack reagent, ItemStack base){
        if(reagent.isEmpty() || base.isEmpty())
            return false;
        GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(level, reagent.getItem(), getTier(base.getItem()));
        if(recipe == null)
            return false;

        int manaCost = recipe.tier == ISpellTier.Tier.ONE ? 500 : (recipe.tier == ISpellTier.Tier.TWO ? 1500 : 3000);
        AtomicBoolean valid = new AtomicBoolean(false);
        BlockPos.betweenClosedStream(this.getBlockPos().offset(5, -3, 5), this.getBlockPos().offset(-5, 3, -5)).forEach(blockPos -> {
            if(!valid.get() && level.getBlockEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) level.getBlockEntity(blockPos)).getCurrentMana() >= manaCost) {
                valid.set(true);
            }
        });
        return valid.get();
    }

    private void addItemHandler(@Nullable Direction side) {
        itemHandlers.put(side, LazyOptional.of(() -> new SidedInvWrapper(this, side)));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandlers.getOrDefault(side, super.getCapability(cap, side).cast()).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        itemHandlers.values().forEach(LazyOptional::invalidate);
        super.invalidateCaps();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        reagentItem = ItemStack.of((CompoundNBT)compound.get("itemStack"));
        baseMaterial = ItemStack.of((CompoundNBT)compound.get("baseMat"));
        oldBaseMat = ItemStack.of((CompoundNBT)compound.get("oldBase"));
        isCrafting = compound.getBoolean("crafting");
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if(reagentItem != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            reagentItem.save(reagentTag);
            compound.put("itemStack", reagentTag);
        }
        if(baseMaterial != null){
            CompoundNBT baseMatTag = new CompoundNBT();
            baseMaterial.save(baseMatTag);
            compound.put("baseMat", baseMatTag);
        }

        if(oldBaseMat != null){
            CompoundNBT baseMatTag = new CompoundNBT();
            oldBaseMat.save(baseMatTag);
            compound.put("oldBase", baseMatTag);
        }
        compound.putBoolean("crafting", isCrafting);
        return super.save(compound);
    }

}
