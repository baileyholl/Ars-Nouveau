package com.hollingsworth.arsnouveau.common.entity.goal.drygmy;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class CollectEssenceGoal extends Goal {

    EntityDrygmy drygmy;
    LivingEntity target;
    boolean complete;
    boolean approached;
    int timeChanneling;

    public CollectEssenceGoal(EntityDrygmy drygmy){
        this.drygmy = drygmy;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return drygmy.channelCooldown <= 0;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        DrygmyTile tile = drygmy.getHome();
        if(tile == null)
            return;
        target = tile.getRandomEntity();
        complete = false;
        approached = false;
        timeChanneling = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !complete && canUse() && target != null && !target.isRemoved() && target.isAlive() && drygmy.getHome() != null;
    }

    @Override
    public void tick() {
        super.tick();
        if(complete || target == null)
            return;

        if(!approached && BlockUtil.distanceFrom(drygmy.position, target.position) >= 2){
            Path path = drygmy.getNavigation().createPath(target.getX(), target.getY(), target.getZ(), 1);
            if(path == null || !path.canReach()){
                approached = true;
                drygmy.getNavigation().stop();
                return;
            }
            drygmy.getNavigation().moveTo(path, 1.0);
        }else{
            drygmy.setChannelingEntity(target.getId());
            drygmy.getLookControl().setLookAt(target, 10.0F, (float)drygmy.getMaxHeadXRot());
            drygmy.getNavigation().stop();
            this.approached = true;
            drygmy.setChanneling(true);
            timeChanneling++;
            if(timeChanneling >= 100){
                drygmy.setChanneling(false);
                drygmy.setHoldingEssence(true);
                drygmy.setChannelingEntity(-1);
                this.complete = true;
                BlockPos homePos = drygmy.getHome().getBlockPos();
                BlockPos targetPos = target.blockPosition();
                if(homePos.getY() >= targetPos.getY() - 2){
                    targetPos = targetPos.above(homePos.getY() - targetPos.getY());
                }

                EntityFlyingItem item = new EntityFlyingItem(drygmy.level,
                        targetPos, homePos,
                        50,
                       255,
                       20);
                drygmy.level.addFreshEntity(item);
                drygmy.channelCooldown = 100;
                drygmy.getHome().giveProgress();
            }
        }
    }


    @Override
    public void stop() {
        super.stop();
        complete = false;
        approached = false;
        drygmy.setChannelingEntity(-1);
        drygmy.setChanneling(false);
        timeChanneling = 0;
    }
}
