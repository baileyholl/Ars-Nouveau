package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.goal.ExtendedRangeGoal;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.EnumSet;

public class FindPotionGoal extends ExtendedRangeGoal {
    EntityWixie wixie;
    BlockPos movePos;
    boolean found;
    PotionContents potionNeeded;

    public FindPotionGoal(EntityWixie wixie) {
        super(15);
        this.wixie = wixie;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public void start() {
        super.start();
        BlockEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);
        found = false;
        if (tileEntity instanceof WixieCauldronTile cauldronTile) {
            potionNeeded = cauldronTile.getNeededPotion();
            movePos = WixieCauldronTile.findNeededPotion(potionNeeded, 300, wixie.level, wixie.cauldronPos);
            this.startDistance = BlockUtil.distanceFrom(wixie.position, movePos);
        } else {
            found = true;
        }

        if (movePos == null)
            found = true;


    }

    @Override
    public void tick() {
        super.tick();
        if (found)
            return;

        if (movePos != null && BlockUtil.distanceFrom(wixie.position(), movePos.above()) < 2.0 + this.extendedRange) {
            WixieCauldronTile tile = (WixieCauldronTile) wixie.getCommandSenderWorld().getBlockEntity(wixie.cauldronPos);
            ServerLevel world = (ServerLevel) wixie.getCommandSenderWorld();
            if (tile == null) {
                found = true;
                return;
            }
            PotionJarTile jar = (PotionJarTile) world.getBlockEntity(movePos);
            if (jar == null) {
                found = true;
                return;
            }
            jar.remove(300);
            tile.givePotion();
            Networking.sendToNearbyClient(world, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.SUMMON_ITEM.ordinal()));
            int color = jar.getColor();
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;
            EntityFollowProjectile.spawn(world, movePos, wixie.cauldronPos, r, g, b);
            found = true;

        }

        if (movePos != null && !found) {
            setPath(movePos.getX(), movePos.getY() + 1, movePos.getZ(), 1.2D);
        }
    }

    @Override
    public boolean canUse() {
        if (wixie.cauldronPos == null)
            return false;
        BlockEntity tileEntity = wixie.level.getBlockEntity(wixie.cauldronPos);

        return wixie.inventoryBackoff == 0 && tileEntity instanceof WixieCauldronTile cauldronTile
                && cauldronTile.hasSource && cauldronTile.needsPotion() && !cauldronTile.isOff;
    }

    @Override
    public boolean canContinueToUse() {
        return !found;
    }

    public void setPath(double x, double y, double z, double speedIn) {
        wixie.getNavigation().moveTo(wixie.getNavigation().createPath(x + 0.5, y + 0.5, z + 0.5, 0), speedIn);
    }

    @Override
    public void stop() {
        super.stop();
        potionNeeded = PotionContents.EMPTY;
        found = false;
    }
}
