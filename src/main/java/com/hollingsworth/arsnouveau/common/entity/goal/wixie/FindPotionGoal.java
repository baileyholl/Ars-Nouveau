package com.hollingsworth.arsnouveau.common.entity.goal.wixie;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class FindPotionGoal extends Goal {
    EntityWixie wixie;
    BlockPos movePos;
    boolean found;
    Potion potionNeeded;

    public FindPotionGoal(EntityWixie wixie){
        this.wixie = wixie;
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        TileEntity tileEntity = wixie.world.getTileEntity(wixie.cauldronPos);
        found = false;
        if(tileEntity instanceof WixieCauldronTile) {
            potionNeeded = ((WixieCauldronTile) tileEntity).getNeededPotion();
            movePos = ((WixieCauldronTile) tileEntity).findNeededPotion(potionNeeded, 300);
        }else{
            found = true;
        }

        if(movePos == null)
            found = true;


    }

    @Override
    public void tick() {
        if(found)
            return;

        if(movePos != null && BlockUtil.distanceFrom(wixie.getPosition(), movePos.up()) < 1.5D){
            WixieCauldronTile tile = (WixieCauldronTile) wixie.getEntityWorld().getTileEntity(wixie.cauldronPos);
            World world = wixie.getEntityWorld();
            if(tile == null) {
                found = true;
                return;
            }
            PotionJarTile jar = (PotionJarTile) world.getTileEntity(movePos);
            if(jar == null){
                found = true;
                return;
            }
            jar.setFill(jar.getCurrentFill() - 300);
            tile.givePotion();
            Networking.sendToNearby(world, wixie, new PacketAnimEntity(wixie.getEntityId(), EntityWixie.Animations.SUMMON_ITEM.ordinal()));
            int color = jar.getColor();
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color >> 0) & 0xFF;
            int a = (color >> 24) & 0xFF;
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, movePos, wixie.cauldronPos, r,g,b);

            world.addEntity(aoeProjectile);
            found= true;

        }

        if(movePos != null && !found) {
            setPath(movePos.getX(), movePos.getY()+1, movePos.getZ(), 1.2D);
        }
    }

    @Override
    public boolean shouldExecute() {
        if(wixie.cauldronPos == null)
            return false;
        TileEntity tileEntity = wixie.world.getTileEntity(wixie.cauldronPos);

        return wixie.inventoryBackoff == 0 && tileEntity instanceof WixieCauldronTile
                && ((WixieCauldronTile) tileEntity).hasMana && ((WixieCauldronTile) tileEntity).needsPotion()  && !((WixieCauldronTile) tileEntity).isOff;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !found;
    }

    public void setPath(double x, double y, double z, double speedIn){
        wixie.getNavigator().setPath( wixie.getNavigator().getPathToPos(x+0.5, y+0.5, z+0.5, 0), speedIn);
    }

    @Override
    public void resetTask() {
        potionNeeded = Potions.EMPTY;
        found = false;
    }
}
