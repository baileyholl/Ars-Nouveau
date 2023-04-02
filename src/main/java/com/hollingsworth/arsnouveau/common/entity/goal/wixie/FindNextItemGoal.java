package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.FlyingItemEvent;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

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
        Level world = wixie.getCommandSenderWorld();
        WixieCauldronTile tile = (WixieCauldronTile) world.getBlockEntity(wixie.cauldronPos);
        if (tile == null || tile.inventories == null) {
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
        for (BlockPos b : tile.inventories) {
            if (!(world.getBlockEntity(b) instanceof Container i))
                continue;
            if (i.hasAnyOf(itemSet)) {
                movePos = b;
                this.startDistance = BlockUtil.distanceFrom(wixie.position, movePos);
                break;
            }
        }
        found = false;
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public boolean canUse() {
        if (wixie.cauldronPos == null)
            return false;
        BlockEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
        return wixie.inventoryBackoff == 0 && tileEntity instanceof WixieCauldronTile cauldronTile
               && cauldronTile.hasSource && !cauldronTile.isCraftingDone() && !cauldronTile.isOff && !cauldronTile.craftManager.getNextItem().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return !found && movePos != null;
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
            for(ItemStack needed : neededStacks) {
                for (BlockPos b : tile.inventories) {
                    if(tile.craftManager.neededItems.isEmpty()){
                        found = true;
                        return;
                    }
                    if (!(world.getBlockEntity(b) instanceof Container i))
                        continue;
                    for (int j = 0; j < i.getContainerSize(); j++) {
                        if (i.getItem(j).getItem() == needed.getItem()) {
                            ItemStack stackToGive = i.getItem(j).copy();
                            spawnFlyingItem(tile.getLevel(), tile.getBlockPos(), b, stackToGive, 1 + 3 * spawnDelay++);
                            stackToGive.setCount(1);
                            tile.giveItem(stackToGive);
                            i.getItem(j).shrink(1);
                            if(!anyFound) {
                                Networking.sendToNearby(world, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.SUMMON_ITEM.ordinal()));
                                wixie.inventoryBackoff = 60;
                                anyFound = true;
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
