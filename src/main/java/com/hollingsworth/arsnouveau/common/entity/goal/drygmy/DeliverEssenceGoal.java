package com.hollingsworth.arsnouveau.common.entity.goal.drygmy;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.BlockPos;

import java.util.EnumSet;

public class DeliverEssenceGoal extends Goal {

    EntityDrygmy drygmy;
    BlockPos target;
    boolean approached;

    public DeliverEssenceGoal(EntityDrygmy drygmy){
        this.drygmy = drygmy;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        return !approached && drygmy.holdingEssence() && drygmy.getHome() != null;
    }

    @Override
    public void stop() {
        super.stop();
        approached = false;
    }

    @Override
    public void start() {
        super.start();
        target = drygmy.getHome() == null ? null : drygmy.getHome().getBlockPos();
        approached = false;
    }

    @Override
    public void tick() {
        super.tick();
        if(!approached && BlockUtil.distanceFrom(drygmy.blockPosition(),target) >= 2){
            Path path = drygmy.getNavigation().createPath(target.getX(), target.getY(), target.getZ(), 1);
            if(path == null || !path.canReach()){
                approached = true;
                return;
            }

            drygmy.getNavigation().moveTo(path, 1.0);
        }else{
            this.approached = true;
            drygmy.getHome().giveProgress();
            drygmy.setHoldingEssence(false);
        }
    }
}
