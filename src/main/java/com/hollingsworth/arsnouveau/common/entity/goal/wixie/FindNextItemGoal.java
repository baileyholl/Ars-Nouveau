package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class FindNextItemGoal extends Goal {
    EntityWixie wixie;
    BlockPos movePos;
    ItemStack getStack;
    boolean found;

    public FindNextItemGoal(EntityWixie wixie){
        this.wixie = wixie;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public void start() {
        World world = wixie.getCommandSenderWorld();
        WixieCauldronTile tile = (WixieCauldronTile) world.getBlockEntity(wixie.cauldronPos);
        if(tile == null || tile.inventories == null) {
            found = true;
            return;
        }
        getStack = tile.craftManager.getNextItem();
        if(getStack.isEmpty()){
            found = true;
            return;
        }
        Set<Item> itemSet = new HashSet<>();
        itemSet.add(getStack.getItem());
        for(BlockPos b : tile.inventories){
            if(!(world.getBlockEntity(b) instanceof IInventory))
                continue;
            IInventory i = (IInventory) world.getBlockEntity(b);
            if(i.hasAnyOf(itemSet)){
                movePos = b;
                break;
            }
        }
        found = false;
    }

    @Override
    public boolean canUse() {
        if(wixie.cauldronPos == null)
            return false;
        TileEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
        return wixie.inventoryBackoff == 0 && tileEntity instanceof WixieCauldronTile
                && ((WixieCauldronTile) tileEntity).hasMana && !((WixieCauldronTile) tileEntity).isCraftingDone() && !((WixieCauldronTile) tileEntity).isOff &&  !((WixieCauldronTile) tileEntity).craftManager.getNextItem().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return !found && movePos != null;
    }

    @Override
    public void tick() {

        if(!found && movePos != null && BlockUtil.distanceFrom(wixie.blockPosition(), movePos.above()) < 1.5D){

            WixieCauldronTile tile = (WixieCauldronTile) wixie.getCommandSenderWorld().getBlockEntity(wixie.cauldronPos);
            World world = wixie.getCommandSenderWorld();
            if(tile == null) {
                found = true;
                return;
            }

            for(BlockPos b : tile.inventories){

                if(!(world.getBlockEntity(b) instanceof IInventory))
                    continue;
                IInventory i = (IInventory) world.getBlockEntity(b);
                for(int j = 0; j < i.getContainerSize(); j++) {
                    if (i.getItem(j).getItem() == getStack.getItem()) {
                        found = true;
                        ItemStack stackToGive = i.getItem(j).copy();
                        tile.spawnFlyingItem(b,stackToGive);
                        stackToGive.setCount(1);
                        tile.giveItem(stackToGive);
                        i.getItem(j).shrink(1);
                        Networking.sendToNearby(world, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.SUMMON_ITEM.ordinal()));
                        wixie.inventoryBackoff = 60;
                        break;
                    }
                }
                if(found)
                    break;
            }
        }

        if(movePos != null && !found) {
            setPath(movePos.getX(), movePos.getY()+1, movePos.getZ(), 1.2D);
        }
    }

    public void setPath(double x, double y, double z, double speedIn){
        wixie.getNavigation().moveTo( wixie.getNavigation().createPath(x+0.5, y+0.5, z+0.5, 0), speedIn);
    }

}
