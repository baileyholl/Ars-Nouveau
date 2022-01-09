package com.hollingsworth.arsnouveau.common.block.tile;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

public class GlyphPressTile extends AnimatedTile implements ITickable, IAnimatable, IAnimationListener, WorldlyContainer {
    private final Map<Direction, LazyOptional<IItemHandler>> itemHandlers = new HashMap<>();
    public long frames = 0;
    public boolean isCrafting;
    public ItemStack reagentItem = ItemStack.EMPTY;
    public ItemStack baseMaterial = ItemStack.EMPTY;
    public ItemStack oldBaseMat = ItemStack.EMPTY;
    public ItemEntity entity;

    public GlyphPressTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.GLYPH_PRESS_TILE, pos, state);
        ImmutableList.of(Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST).forEach(this::addItemHandler);
        addItemHandler(null);
    }

    @Override
    public void tick() {
        if(!level.isClientSide && level.getGameTime() % 20 == 0 && canCraft(reagentItem, baseMaterial))
            craft(FakePlayerFactory.getMinecraft((ServerLevel) level));

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


    public boolean craft(Player playerEntity) {
        if(isCrafting || baseMaterial == ItemStack.EMPTY)
            return false;
        GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(level, reagentItem.getItem(), getTier(this.baseMaterial.getItem()));
        if(recipe == null)
            return false;

        int manaCost = recipe.tier == SpellTier.ONE ? 1000 : (recipe.tier == SpellTier.TWO ? 2000 : 3000);
        BlockPos jar = SourceUtil.takeSourceNearbyWithParticles(worldPosition, level, 5, manaCost);
        if(jar != null){
            isCrafting = true;
            Networking.sendToNearby(level, worldPosition, new PacketOneShotAnimation(worldPosition));
            return true;
        }

        playerEntity.sendMessage(new TranslatableComponent("ars_nouveau.glyph_press.no_mana"), Util.NIL_UUID);
        return false;
    }


    public SpellTier getTier(Item clay){
        if(clay == ItemsRegistry.MAGIC_CLAY)
            return SpellTier.ONE;
        else if(clay == ItemsRegistry.MARVELOUS_CLAY){
            return SpellTier.TWO;
        }else if(clay == ItemsRegistry.MYTHICAL_CLAY)
            return SpellTier.THREE;
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

    private <E extends BlockEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void startAnimation(int arg) {
        AnimationData data = this.manager.getOrCreateAnimationData(this.hashCode());
        data.setResetSpeedInTicks(0.0);
        AnimationController controller = data.getAnimationControllers().get("controller");
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
    public boolean stillValid(Player player) {
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

        int manaCost = recipe.tier == SpellTier.ONE ? 500 : (recipe.tier == SpellTier.TWO ? 1500 : 3000);
        AtomicBoolean valid = new AtomicBoolean(false);
        BlockPos.betweenClosedStream(this.getBlockPos().offset(5, -3, 5), this.getBlockPos().offset(-5, 3, -5)).forEach(blockPos -> {
            if(!valid.get() && level.getBlockEntity(blockPos) instanceof SourceJarTile && ((SourceJarTile) level.getBlockEntity(blockPos)).getSource() >= manaCost) {
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
    public void invalidateCaps() {
        itemHandlers.values().forEach(LazyOptional::invalidate);
        super.invalidateCaps();
    }

    @Override
    public void load(CompoundTag compound) {
        reagentItem = ItemStack.of((CompoundTag)compound.get("itemStack"));
        baseMaterial = ItemStack.of((CompoundTag)compound.get("baseMat"));
        oldBaseMat = ItemStack.of((CompoundTag)compound.get("oldBase"));
        isCrafting = compound.getBoolean("crafting");
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(reagentItem != null) {
            CompoundTag reagentTag = new CompoundTag();
            reagentItem.save(reagentTag);
            tag.put("itemStack", reagentTag);
        }
        if(baseMaterial != null){
            CompoundTag baseMatTag = new CompoundTag();
            baseMaterial.save(baseMatTag);
            tag.put("baseMat", baseMatTag);
        }

        if(oldBaseMat != null){
            CompoundTag baseMatTag = new CompoundTag();
            oldBaseMat.save(baseMatTag);
            tag.put("oldBase", baseMatTag);
        }
        tag.putBoolean("crafting", isCrafting);
    }

}
