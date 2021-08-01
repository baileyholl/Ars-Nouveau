package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tileentity.TileEntity;

public class CompleteCraftingGoal extends Goal {
    EntityWixie wixie;
    int ticksNearby;
    boolean hasCast;

    public CompleteCraftingGoal(EntityWixie wixie){
        this.wixie = wixie;
    }

    @Override
    public void start() {
        ticksNearby = 0;
        hasCast = false;
    }

    @Override
    public boolean canUse() {
        if(wixie.cauldronPos == null)
            return false;

        TileEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
        return tileEntity instanceof WixieCauldronTile && ((WixieCauldronTile) tileEntity).isCraftingDone();
    }

    @Override
    public void tick() {
        if(BlockUtil.distanceFrom(wixie.blockPosition(), wixie.cauldronPos.above()) < 1.5D){
            ticksNearby++;
            if(!hasCast){
                Networking.sendToNearby(wixie.level, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.CAST.ordinal()));
                wixie.inventoryBackoff = 40;
            }
            if(ticksNearby >= 40){
                TileEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
                if(tileEntity instanceof WixieCauldronTile && ((WixieCauldronTile) tileEntity).isCraftingDone()){
                    ((WixieCauldronTile) tileEntity).attemptFinish();
                }
            }
            hasCast = true;
        }else{
            setPath(wixie.cauldronPos.getX(), wixie.cauldronPos.getY(), wixie.cauldronPos.getZ(), 1.2D);
        }
    }



    public void setPath(double x, double y, double z, double speedIn){
        wixie.getNavigation().moveTo( wixie.getNavigation().createPath(x+0.5, y+1.5, z+0.5, 0), speedIn);
    }
}
