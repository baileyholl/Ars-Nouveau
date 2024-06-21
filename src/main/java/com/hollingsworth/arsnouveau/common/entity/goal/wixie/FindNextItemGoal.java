package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.FlyingItemEvent;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import java.util.*;

public class FindNextItemGoal extends ExtendedRangeGoal {
    EntityWixie wixie;
    BlockPos movePos;
    ItemStack getStack;
    boolean found;

    public FindNextItemGoal(EntityWixie wixie) {
        super(10);
        this.wixie = wixie;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public void start() {
        super.start();
        movePos = null;
        Level world = wixie.getCommandSenderWorld();
        WixieCauldronTile tile = (WixieCauldronTile) world.getBlockEntity(wixie.cauldronPos);
        if (tile == null || tile.getInventories() == null) {
            found = true;
            return;
        }
        getStack = tile.craftManager.getNextItem();
        if (getStack.isEmpty()) {
            found = true;
            return;
        }
        Set<Item> itemSet = new HashSet<>();
        itemSet.add(getStack.getItem());
        for (BlockPos b : tile.getInventories()) {
            BlockEntity blockEntity = world.getBlockEntity(b);
            if(blockEntity == null)
                continue;
            IItemHandler itemHandler = blockEntity.getCapability(Capabilities.ITEM_HANDLER).orElse(null);
            if (itemHandler == null)
                continue;
            for(int i = 0; i < itemHandler.getSlots(); i++){
                ItemStack stack = itemHandler.getStackInSlot(i);
                if(stack.getItem() == getStack.getItem()){
                    movePos = b.immutable();
                    this.startDistance = BlockUtil.distanceFrom(wixie.position, movePos);
                    break;
                }
            }
            if(movePos != null){
                break;
            }
        }
        found = false;
    }

    @Override
    public boolean canUse() {
        if (wixie.cauldronPos == null)
            return false;
        BlockEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
        if (!(tileEntity instanceof WixieCauldronTile cauldronTile))
            return false;

        boolean canStart = wixie.inventoryBackoff == 0
                && cauldronTile.hasSource
                && !cauldronTile.isCraftingDone()
                && !cauldronTile.isOff
                && !cauldronTile.craftManager.getNextItem().isEmpty();
        return canStart;
    }

    @Override
    public boolean canContinueToUse() {
        return !found;
    }

    @Override
    public void tick() {
        super.tick();

        if (!found && movePos != null && BlockUtil.distanceFrom(wixie.position(), movePos.above()) < 2.0 + this.extendedRange) {

            WixieCauldronTile tile = (WixieCauldronTile) wixie.getCommandSenderWorld().getBlockEntity(wixie.cauldronPos);
            Level world = wixie.getCommandSenderWorld();
            if (tile == null) {
                found = true;
                return;
            }
            List<ItemStack> neededStacks = new ArrayList<>(tile.craftManager.neededItems);
            boolean anyFound = false;
            int spawnDelay = 0;
            List<StorageLecternTile.HandlerPos> handlers = new ArrayList<>();
            for (BlockPos b : tile.getInventories()) {
                BlockEntity blockEntity = world.getBlockEntity(b);
                if(blockEntity == null)
                    continue;
                IItemHandler itemHandler = blockEntity.getCapability(Capabilities.ITEM_HANDLER).orElse(null);
                if (itemHandler == null)
                    continue;
               handlers.add(new StorageLecternTile.HandlerPos(b.immutable(), itemHandler));
            }
            for(ItemStack needed : neededStacks) {
                for (StorageLecternTile.HandlerPos handler : handlers) {
                    if(tile.craftManager.neededItems.isEmpty()){
                        found = true;
                        return;
                    }
                    IItemHandler itemHandler = handler.handler();
                    for (int j = 0; j < itemHandler.getSlots(); j++) {
                        ItemStack slotStack = itemHandler.getStackInSlot(j);
                        if (slotStack.getItem() == needed.getItem()) {
                            // Extract from the same slot multiple times
                            int size = slotStack.getCount();
                            int numNeeded = (int) tile.craftManager.neededItems.stream().filter(stack -> stack.getItem() == slotStack.getItem()).count();
                            int canExtract = Math.min(size, numNeeded);
                            for(int count = 0; count < canExtract; count++) {
                                ItemStack stackToGive = itemHandler.extractItem(j, 1, false);
                                spawnFlyingItem(tile.getLevel(), tile.getBlockPos(), handler.pos(), stackToGive, 1 + 3 * spawnDelay++);
                                tile.giveItem(stackToGive);
                                if (!anyFound) {
                                    Networking.sendToNearby(world, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.SUMMON_ITEM.ordinal()));
                                    wixie.inventoryBackoff = 60;
                                    anyFound = true;
                                }
                            }
                        }
                    }
                }
            }
            found = true;
            return;
        }

        if (movePos != null && !found) {
            setPath(movePos.getX(), movePos.getY() + 1, movePos.getZ(), 1.2D);
        }
    }

    public void spawnFlyingItem(Level level, BlockPos worldPosition, BlockPos from, ItemStack stack, int delay) {
        BlockPos above = from.above();
        EntityFlyingItem flyingItem = new EntityFlyingItem(level,
                new Vec3(above.getX() + 0.5, above.getY(), above.getZ() + 0.5).add(ParticleUtil.inRange(-0.25, 0.25), 0, ParticleUtil.inRange(-0.25, 0.25)),
                new Vec3(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5).add(ParticleUtil.inRange(-0.25, 0.25), 0, ParticleUtil.inRange(-0.25, 0.25)));
        flyingItem.getEntityData().set(EntityFlyingItem.HELD_ITEM, stack.copy());
        EventQueue.getServerInstance().addEvent(new FlyingItemEvent(level, flyingItem, delay));
    }


    public void setPath(double x, double y, double z, double speedIn) {
        wixie.getNavigation().moveTo(wixie.getNavigation().createPath(x + 0.5, y + 0.5, z + 0.5, 0), speedIn);
    }

}
