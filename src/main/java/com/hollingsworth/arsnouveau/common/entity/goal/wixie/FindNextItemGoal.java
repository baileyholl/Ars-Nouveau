package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class FindNextItemGoal extends Goal {
    EntityWixie wixie;
    BlockPos movePos;
    ItemStack getStack;
    boolean found;


    public FindNextItemGoal(EntityWixie wixie){
        this.wixie = wixie;
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        World world = wixie.getEntityWorld();
        WixieCauldronTile tile = (WixieCauldronTile) world.getTileEntity(wixie.cauldronPos);

        if(tile == null || tile.inventories == null) {
            found = true;
            return;
        }
        getStack = tile.getNextRequiredItem();
        if(getStack == null || getStack.isEmpty()){
            found = true;
            return;
        }
        Set<Item> itemSet = new HashSet<>();
        itemSet.add(getStack.getItem());
        for(BlockPos b : tile.inventories){
            if(!(world.getTileEntity(b) instanceof IInventory))
                continue;
            IInventory i = (IInventory) world.getTileEntity(b);
            if(i.hasAny(itemSet)){
                movePos = b;
                break;
            }
        }
        found = false;
    }

    @Override
    public boolean shouldExecute() {
        return wixie.inventoryBackoff == 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !found;
    }

    @Override
    public void tick() {

        if(!found && movePos != null && BlockUtil.distanceFrom(wixie.getPosition(), movePos.up()) < 1.5D){
            WixieCauldronTile tile = (WixieCauldronTile) wixie.getEntityWorld().getTileEntity(wixie.cauldronPos);
            World world = wixie.getEntityWorld();
            if(tile == null) {
                found = true;
                return;
            }

            for(BlockPos b : tile.inventories){
                if(!(world.getTileEntity(b) instanceof IInventory))
                    continue;
                IInventory i = (IInventory) world.getTileEntity(b);
                for(int j = 0; j < i.getSizeInventory(); j++) {
                    if (i.getStackInSlot(j).getItem() == getStack.getItem()) {
                        found = true;
                        System.out.println("item found");
                        ItemStack stackToGive = i.getStackInSlot(j).copy();
                        tile.spawnFlyingItem(b,stackToGive);
                        stackToGive.setCount(1);
                        tile.giveItem(stackToGive);
                        i.getStackInSlot(j).shrink(1);
                        wixie.inventoryBackoff = 40;
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
        wixie.getNavigator().setPath( wixie.getNavigator().getPathToPos(x+0.5, y+0.5, z+0.5, 0), speedIn);
    }

}
