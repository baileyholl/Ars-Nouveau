package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CompleteCraftingGoal extends ExtendedRangeGoal {
    EntityWixie wixie;
    int ticksNearby;
    boolean hasCast;

    public CompleteCraftingGoal(EntityWixie wixie) {
        super(10);
        this.wixie = wixie;
    }

    @Override
    public void start() {
        super.start();
        ticksNearby = 0;
        hasCast = false;
        this.startDistance = BlockUtil.distanceFrom(wixie.position, wixie.cauldronPos.above());
    }

    @Override
    public boolean canUse() {
        if (wixie.cauldronPos == null)
            return false;

        BlockEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
        return tileEntity instanceof WixieCauldronTile && ((WixieCauldronTile) tileEntity).isCraftingDone();
    }

    @Override
    public void tick() {
        super.tick();
        if (BlockUtil.distanceFrom(wixie.position(), wixie.cauldronPos.above()) < 1.5D + this.extendedRange) {
            ticksNearby++;
            if (!hasCast) {
//                Networking.sendToNearby(wixie.level, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.CAST.ordinal()));
                wixie.inventoryBackoff = 40;
            }
            if (ticksNearby >= 40) {
                BlockEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
                if (tileEntity instanceof WixieCauldronTile cauldronTile && cauldronTile.isCraftingDone()) {
                    cauldronTile.attemptFinish();
                }
            }
            hasCast = true;
        } else {
            setPath(wixie.cauldronPos.getX(), wixie.cauldronPos.getY(), wixie.cauldronPos.getZ(), 1.2D);
        }
    }


    public void setPath(double x, double y, double z, double speedIn) {
        wixie.getNavigation().moveTo(wixie.getNavigation().createPath(x + 0.5, y + 1.5, z + 0.5, 0), speedIn);
    }
}
