package com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Supplier;

public class RandomStorageVisitGoal extends Goal {
    public BlockPos target;
    public int ticksRunning;
    public boolean arrived;
    public int arrivedTicks;
    public boolean isDone;
    public EntityBookwyrm bookwyrm;
    public Supplier<BlockPos> getTarget;

    public RandomStorageVisitGoal(EntityBookwyrm bookwyrm, Supplier<BlockPos> getTarget) {
        this.bookwyrm = bookwyrm;
        this.getTarget = getTarget;

        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        target = getTarget.get();
        ticksRunning = 0;
        arrived = false;
        arrivedTicks = 0;
        isDone = false;
        bookwyrm.backoffTicks = 100 + bookwyrm.level.random.nextInt(100);
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }
        ticksRunning++;
        if (!arrived) {
            if (BlockUtil.distanceFrom(bookwyrm.position, new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5)) < 1.5) {
                arrived = true;
            }
            bookwyrm.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 1.2d);
        } else {
            arrivedTicks++;
            if (arrivedTicks > 100) {
                isDone = true;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && ticksRunning < 10 * 20 && !isDone && !bookwyrm.playerTooFar;
    }

    @Override
    public boolean canUse() {
        return !bookwyrm.playerTooFar && bookwyrm.backoffTicks <= 0 && bookwyrm.getRandom().nextInt(4) == 0;
    }
}

