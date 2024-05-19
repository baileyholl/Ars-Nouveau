package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IStateEvent;
import com.hollingsworth.arsnouveau.common.entity.statemachine.SimpleStateMachine;
import com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle.DecideStarbyActionState;
import com.hollingsworth.arsnouveau.common.entity.statemachine.starbuncle.StarbyState;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class StarbyTransportBehavior extends StarbyListBehavior {
    public static Cache<BlockPos, List<ItemEntity>> frameCache = CacheBuilder.newBuilder()
            .expireAfterAccess(20, TimeUnit.SECONDS)
            .build();

    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(ArsNouveau.MODID, "starby_transport");

    public ItemStack itemScroll;

    public SimpleStateMachine<StarbyState, IStateEvent> stateMachine;

    public int berryBackoff;
    public int findItemBackoff;
    public int takeItemBackoff;
    public StarbyTransportBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        stateMachine = new SimpleStateMachine<>(new DecideStarbyActionState(starbuncle, this));
        if (!entity.isTamed())
            return;

        if (tag.contains("itemScroll"))
            this.itemScroll = ItemStack.of(tag.getCompound("itemScroll"));
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide) {
            if(berryBackoff > 0){
                berryBackoff--;
            }
            if(findItemBackoff > 0){
                findItemBackoff--;
            }
            if(takeItemBackoff > 0){
                takeItemBackoff--;
            }
            stateMachine.tick();
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ItemScroll scroll) {
            this.itemScroll = stack.copy();
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            syncTag();
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        super.pickUpItem(itemEntity);
        if (getValidStorePos(itemEntity.getItem()) == null || isPickupDisabled())
            return;
        Starbuncle starbuncleWithRoom = starbuncle.getStarbuncleWithSpace();
        starbuncleWithRoom.setHeldStack(itemEntity.getItem());
        itemEntity.remove(Entity.RemovalReason.DISCARDED);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, starbuncle.getSoundSource(), 1.0F, 1.0F);
        for (ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, starbuncle.getBoundingBox().inflate(3))) {
            if (itemEntity.getItem().getCount() >= itemEntity.getItem().getMaxStackSize())
                break;
            int maxTake = starbuncleWithRoom.getHeldStack().getMaxStackSize() - starbuncleWithRoom.getHeldStack().getCount();
            if (ItemStack.isSameItemSameTags(i.getItem(), starbuncleWithRoom.getHeldStack())) {
                int toTake = Math.min(i.getItem().getCount(), maxTake);
                i.getItem().shrink(toTake);
                starbuncleWithRoom.getHeldStack().grow(toTake);
            }
        }
    }

    public BlockPos getValidStorePos(ItemStack stack) {
        if (TO_LIST.isEmpty() || stack.isEmpty())
            return null;
        BlockPos returnPos = null;
        ItemScroll.SortPref foundPref = ItemScroll.SortPref.INVALID;

        for (BlockPos b : TO_LIST) {
            ItemScroll.SortPref pref = sortPrefForStack(b, stack);
            // Pick our highest priority
            if (pref.ordinal() > foundPref.ordinal()) {
                foundPref = pref;
                returnPos = b;
                if (foundPref == ItemScroll.SortPref.HIGHEST) {
                    return returnPos;
                }
            }
        }
        return returnPos;
    }

    public ItemScroll.SortPref sortPrefForStack(@Nullable BlockPos b, ItemStack stack) {
        if (stack == null || stack.isEmpty() || b == null || !level.isLoaded(b))
            return ItemScroll.SortPref.INVALID;
        return canDepositItem(level.getBlockEntity(b), stack);
    }

    public boolean isPickupDisabled() {
        return starbuncle.getCosmeticItem().getItem() == ItemsRegistry.STARBUNCLE_SHADES.get();
    }

    public @Nullable IItemHandler getItemCapFromTile(BlockEntity blockEntity, @Nullable Direction face) {
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, face).isPresent()) {
            var lazy = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, face).resolve();
            if (lazy.isPresent())
                return lazy.get();
        }
        return null;
    }

    public @Nullable BlockPos getValidTakePos() {
        if (FROM_LIST.isEmpty())
            return null;

        for (BlockPos p : FROM_LIST) {
            if (isPositionValidTake(p))
                return p;
        }
        return null;
    }

    public boolean isPositionValidTake(BlockPos p) {

        if (p == null || !level.isLoaded(p)) return false;
        Direction face = FROM_DIRECTION_MAP.get(p.hashCode());
        IItemHandler iItemHandler = getItemCapFromTile(level.getBlockEntity(p), face);

        if (iItemHandler == null) return false;
        for (int j = 0; j < iItemHandler.getSlots(); j++) {
            ItemStack stack = iItemHandler.getStackInSlot(j);
            if (!stack.isEmpty() && getValidStorePos(stack) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the maximum stack size an inventory can accept for a particular stack. Does all needed validity checks.
     */
    public int getMaxTake(ItemStack stack) {
        if (getValidStorePos(stack) == null) {
            return -1;
        }
        BlockPos validStorePos = getValidStorePos(stack);
        if (validStorePos == null) return -1;
        IItemHandler handler = getItemCapFromTile(level.getBlockEntity(validStorePos), FROM_DIRECTION_MAP.get(validStorePos.hashCode()));
        if (handler == null)
            return -1;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack handlerStack = handler.getStackInSlot(i);
            if (handlerStack.isEmpty()) {
                return handler.getSlotLimit(i);
            } else if (ItemHandlerHelper.canItemStacksStack(handler.getStackInSlot(i), stack)) {
                int originalCount = stack.getCount();
                ItemStack simStack = handler.insertItem(i, stack, true);
                int maxRoom = originalCount - simStack.getCount();
                if (maxRoom > 0) {
                    return Math.min(maxRoom, handler.getSlotLimit(i));
                }
            }
        }
        return -1;
    }

    private ItemScroll.SortPref canDepositItem(BlockEntity tile, ItemStack stack) {
        ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
        if (tile == null || stack == null || stack.isEmpty())
            return ItemScroll.SortPref.INVALID;

        IItemHandler handler = getItemCapFromTile(tile, TO_DIRECTION_MAP.get(tile.getBlockPos().hashCode()));
        if (handler == null)
            return ItemScroll.SortPref.INVALID;
        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(tile.getBlockPos()).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity adjTile = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            if (adjTile == null || !adjTile.equals(tile) || i.getItem().isEmpty())
                continue;


            ItemStack stackInFrame = i.getItem();

            if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                pref = scrollItem.getSortPref(stack, stackInFrame, handler);
                // If our item frame just contains a normal item
            } else if (i.getItem().getItem() != stack.getItem()) {
                return ItemScroll.SortPref.INVALID;
            } else if (i.getItem().getItem() == stack.getItem()) {
                pref = ItemScroll.SortPref.HIGHEST;
            }
        }
        if (itemScroll != null && itemScroll.getItem() instanceof ItemScroll scrollItem && scrollItem.getSortPref(stack, itemScroll,
                handler) == ItemScroll.SortPref.INVALID) {
            return ItemScroll.SortPref.INVALID;
        }
        return !ItemStack.matches(ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true), stack) ? pref : ItemScroll.SortPref.INVALID;
    }

    @Override
    public boolean canGoToBed() {
        return isBedPowered() || (getValidTakePos() == null && (starbuncle.getHeldStack().isEmpty() || getValidStorePos(starbuncle.getHeldStack()) == null));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity);
        if (storedPos == null)
            return;
        BlockEntity blockEntity = level.getBlockEntity(storedPos);
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).isPresent()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.store"));
            addToPos(storedPos, side);
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if (storedPos == null)
            return;

        BlockEntity blockEntity = level.getBlockEntity(storedPos);
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).isPresent()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.take"));
            addFromPos(storedPos, side);
        }
    }

    @Override
    public void onWanded(Player playerEntity) {
        this.itemScroll = ItemStack.EMPTY;
        super.onWanded(playerEntity);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (itemScroll != null) {
            tag.put("itemScroll", itemScroll.serializeNBT());
        }
        return tag;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.storing", TO_LIST.size()));
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.taking", FROM_LIST.size()));
        if (itemScroll != null && !itemScroll.isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.filtering_with", itemScroll.getHoverName().getString()));
        }
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }
}
