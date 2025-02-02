package com.hollingsworth.arsnouveau.common.entity.goal.drygmy;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class CollectEssenceGoal extends Goal {

    public EntityDrygmy drygmy;
    public LivingEntity target;
    public boolean complete;
    public boolean approached;
    public int timeChanneling;
    public int timePathing;

    public CollectEssenceGoal(EntityDrygmy drygmy) {
        this.drygmy = drygmy;
    }

    @Override
    public boolean canUse() {
        return drygmy.channelCooldown <= 0 && !drygmy.isPassenger() && drygmy.getHome() != null && !drygmy.getHome().isOff;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        DrygmyTile tile = drygmy.getHome();
        if (tile == null)
            return;
        target = tile.getRandomEntity();
        complete = false;
        approached = false;
        timeChanneling = 0;
        timePathing = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !complete && canUse() && target != null && !target.isRemoved() && target.isAlive();
    }

    @Override
    public void tick() {
        super.tick();

        if (complete || target == null)
            return;

        if (approached) {
            drygmy.setChannelingEntity(target.getId());
            drygmy.setLookAt(target, 10.0F, (float) drygmy.getMaxHeadXRot());
            drygmy.getNavigation().stop();
            drygmy.setChanneling(true);
            timeChanneling++;
            if (timeChanneling >= 100) {
                drygmy.setChanneling(false);
                drygmy.setHoldingEssence(true);
                drygmy.setChannelingEntity(-1);
                this.complete = true;
                BlockPos homePos = drygmy.getHome().getBlockPos();
                BlockPos targetPos = target.blockPosition().above(1);
                if (homePos.getY() >= targetPos.getY() - 2) {
                    targetPos = targetPos.above(homePos.getY() - targetPos.getY());
                }
                EntityFlyingItem.spawn(homePos, (ServerLevel) drygmy.level,
                        targetPos, homePos,
                        50,
                        255,
                        20);

                drygmy.channelCooldown = 100;
                drygmy.giveProgress();
            }
        } else {
            if(timePathing > 20 * 8){
                approached = true;
                drygmy.getNavigation().stop();
            }
            if(BlockUtil.distanceFrom(drygmy.position, target.position) <= 2.3){
                approached = true;
            }else{
                timePathing++;
                Path path = drygmy.getNavigation().createPath(target.getX(), target.getY(), target.getZ(), 1);
                if (path == null || !path.canReach()) {
                    approached = true;
                    drygmy.getNavigation().stop();
                }else {
                    drygmy.getNavigation().moveTo(path, 1.0);
                }
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
