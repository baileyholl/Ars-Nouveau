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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class FindPotionGoal extends ExtendedRangeGoal {
    EntityWixie wixie;
    BlockPos movePos;
    boolean found;
    Potion potionNeeded;

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
        if (tileEntity instanceof WixieCauldronTile) {
            potionNeeded = ((WixieCauldronTile) tileEntity).getNeededPotion();
            movePos = ((WixieCauldronTile) tileEntity).findNeededPotion(potionNeeded, 300);
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
            Level world = wixie.getCommandSenderWorld();
            if (tile == null) {
                found = true;
                return;
            }
            PotionJarTile jar = (PotionJarTile) world.getBlockEntity(movePos);
            if (jar == null) {
                found = true;
                return;
            }
            jar.setFill(jar.getCurrentFill() - 300);
            tile.givePotion();
            Networking.sendToNearby(world, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.SUMMON_ITEM.ordinal()));
            int color = jar.getColor();
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;
            int a = (color >> 24) & 0xFF;
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, movePos, wixie.cauldronPos, r, g, b);

            world.addFreshEntity(aoeProjectile);
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

        return wixie.inventoryBackoff == 0 && tileEntity instanceof WixieCauldronTile
                && ((WixieCauldronTile) tileEntity).hasSource && ((WixieCauldronTile) tileEntity).needsPotion() && !((WixieCauldronTile) tileEntity).isOff;
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
        potionNeeded = Potions.EMPTY;
        found = false;
    }
}
