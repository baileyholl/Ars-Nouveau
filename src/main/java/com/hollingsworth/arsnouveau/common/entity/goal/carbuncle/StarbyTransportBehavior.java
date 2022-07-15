package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.entity.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StarbyTransportBehavior extends StarbyBehavior {
    public static final ResourceLocation TRANSPORT_ID = new ResourceLocation(ArsNouveau.MODID, "starby_transport");

    public List<BlockPos> FROM_LIST = new ArrayList<>();

    public List<BlockPos> TO_LIST = new ArrayList<>();

    public ItemStack itemScroll;

    public StarbyTransportBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        if(!entity.isTamed())
            return;
        int counter = 0;

        while (NBTUtil.hasBlockPos(tag, "from_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "from_" + counter);
            if (!this.FROM_LIST.contains(pos))
                this.FROM_LIST.add(pos);
            counter++;
        }

        counter = 0;
        while (NBTUtil.hasBlockPos(tag, "to_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "to_" + counter);
            if (!this.TO_LIST.contains(pos))
                this.TO_LIST.add(pos);
            counter++;
        }
        if(tag.contains("itemScroll"))
            this.itemScroll = ItemStack.of(tag.getCompound("itemScroll"));
        goals.add(new WrappedGoal(1, new FindItem(starbuncle, this)));
        goals.add(new WrappedGoal(2, new ForageManaBerries(starbuncle, this)));
        goals.add(new WrappedGoal(3, new StoreItemGoal(starbuncle, this)));
        goals.add(new WrappedGoal(3, new TakeItemGoal(starbuncle, this)));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.getItem() instanceof ItemScroll scroll){
            this.itemScroll = stack.copy();
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.filter_set"));
            syncTag();
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void pickUpItem(ItemEntity itemEntity) {
        super.pickUpItem(itemEntity);
        if(!canStoreStack(itemEntity.getItem()))
            return;
        starbuncle.setHeldStack(itemEntity.getItem());
        itemEntity.remove(Entity.RemovalReason.DISCARDED);
        this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, starbuncle.getSoundSource(), 1.0F, 1.0F);
        for (ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, starbuncle.getBoundingBox().inflate(3))) {
            if (itemEntity.getItem().getCount() >= itemEntity.getItem().getMaxStackSize())
                break;
            int maxTake = starbuncle.getHeldStack().getMaxStackSize() - starbuncle.getHeldStack().getCount();
            if (i.getItem().sameItem(starbuncle.getHeldStack())) {
                int toTake = Math.min(i.getItem().getCount(), maxTake);
                i.getItem().shrink(toTake);
                starbuncle.getHeldStack().grow(toTake);
            }
        }
    }

    public BlockPos getValidStorePos(ItemStack stack) {
        if (TO_LIST.isEmpty() || stack.isEmpty())
            return null;
        BlockPos returnPos = null;
        ItemScroll.SortPref foundPref = ItemScroll.SortPref.INVALID;
        for (BlockPos b : TO_LIST) {
            ItemScroll.SortPref pref = isValidStorePos(b, stack);
            // Pick our highest priority
            if (pref.ordinal() > foundPref.ordinal()) {
                foundPref = pref;
                returnPos = b;
            }
        }
        return returnPos;
    }

    public ItemScroll.SortPref isValidStorePos(@Nullable BlockPos b, ItemStack stack) {
        if (stack == null || stack.isEmpty() || b == null)
            return ItemScroll.SortPref.INVALID;
        return canDepositItem(level.getBlockEntity(b), stack);
    }

    public boolean isPickupDisabled() {
        return starbuncle.getCosmeticItem().getItem() == ItemsRegistry.STARBUNCLE_SHADES.get();
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
        if (p == null || level.getBlockEntity(p) == null)
            return false;
        IItemHandler iItemHandler = level.getBlockEntity(p).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (iItemHandler == null)
            return false;
        for (int j = 0; j < iItemHandler.getSlots(); j++) {
            if (!iItemHandler.getStackInSlot(j).isEmpty() && canStoreStack(iItemHandler.getStackInSlot(j)) && getValidStorePos(iItemHandler.getStackInSlot(j)) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the maximum stack size an inventory can accept for a particular stack. Does all needed validity checks.
     */
    public int getMaxTake(ItemStack stack) {
        if (!canStoreStack(stack)) {
            return -1;
        }
        BlockPos validStorePos = getValidStorePos(stack);
        if (validStorePos == null)
            return -1;
        IItemHandler handler = level.getBlockEntity(validStorePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (handler == null)
            return -1;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack handlerStack = handler.getStackInSlot(i);
            if(handlerStack.isEmpty()){
                return handler.getSlotLimit(i);
            }else if (ItemHandlerHelper.canItemStacksStack(handler.getStackInSlot(i), stack)) {
                int maxRoom = handlerStack.getMaxStackSize() - handlerStack.getCount();
                if (maxRoom > 0) {
                    return Math.min(maxRoom, handler.getSlotLimit(i));
                }
            }
        }
        return -1;
    }

    public boolean canStoreStack(ItemStack stack) {
        BlockPos storePos = getValidStorePos(stack);
        if (storePos == null) {
            return false;
        }
        if(itemScroll != null && itemScroll.getItem() instanceof ItemScroll scrollItem && scrollItem.getSortPref(stack, itemScroll,
                level.getBlockEntity(storePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null)) == ItemScroll.SortPref.INVALID) {
            return false;
        }

        return true;
    }

    private ItemScroll.SortPref canDepositItem(BlockEntity tile, ItemStack stack) {
        ItemScroll.SortPref pref = ItemScroll.SortPref.LOW;
        if (tile == null || stack == null || stack.isEmpty())
            return ItemScroll.SortPref.INVALID;

        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
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
        return !ItemStack.matches(ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true), stack) ? pref : ItemScroll.SortPref.INVALID;
    }

    @Override
    public boolean canGoToBed() {
        return getValidTakePos() != null ||
                (!starbuncle.getHeldStack().isEmpty() && getValidStorePos(starbuncle.getHeldStack()) != null);
    }

    @Override
    public void onFinishedConnectionFirst(@org.jetbrains.annotations.Nullable BlockPos storedPos, @org.jetbrains.annotations.Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
        if (storedPos == null)
            return;
        if (level.getBlockEntity(storedPos) != null && level.getBlockEntity(storedPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.store"));
            addToPos(storedPos);
        }
    }

    @Override
    public void onFinishedConnectionLast(@org.jetbrains.annotations.Nullable BlockPos storedPos, @org.jetbrains.annotations.Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if (storedPos == null)
            return;

        if (level.getBlockEntity(storedPos) != null && level.getBlockEntity(storedPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.take"));
            addFromPos(storedPos);
        }
    }

    @Override
    public void onWanded(Player playerEntity) {
        super.onWanded(playerEntity);
        FROM_LIST = new ArrayList<>();
        TO_LIST = new ArrayList<>();
    }

    public void addFromPos(BlockPos fromPos) {
        if (!FROM_LIST.contains(fromPos)) {
            FROM_LIST.add(fromPos.immutable());
            syncTag();
        }
    }

    public void addToPos(BlockPos toPos) {
        if (!TO_LIST.contains(toPos)) {
            TO_LIST.add(toPos.immutable());
            syncTag();
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        int counter = 0;
        for (BlockPos p : FROM_LIST) {
            NBTUtil.storeBlockPos(tag, "from_" + counter, p);
            counter++;
        }
        counter = 0;
        for (BlockPos p : TO_LIST) {
            NBTUtil.storeBlockPos(tag, "to_" + counter, p);
            counter++;
        }
        if(itemScroll != null) {
            tag.put("itemScroll", itemScroll.serializeNBT());
        }
        return tag;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        StarbyTransportBehavior behavior = (StarbyTransportBehavior) BehaviorRegistry.create(starbuncle, starbuncle.getEntityData().get(Starbuncle.BEHAVIOR_TAG));
        if(behavior != null){
            tooltip.add(Component.translatable("ars_nouveau.starbuncle.storing", behavior.TO_LIST.size()));
            tooltip.add(Component.translatable("ars_nouveau.starbuncle.taking", behavior.FROM_LIST.size()));
            if(behavior.itemScroll != null && !behavior.itemScroll.isEmpty()) {
                tooltip.add(Component.translatable("ars_nouveau.filtering_with", behavior.itemScroll.getHoverName().getString()));
            }
        }

    }

    public void syncTag(){
        starbuncle.getEntityData().set(Starbuncle.BEHAVIOR_TAG, this.toTag(new CompoundTag()));
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return TRANSPORT_ID;
    }
}
