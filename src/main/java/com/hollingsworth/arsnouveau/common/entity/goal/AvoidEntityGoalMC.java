package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Predicate;

public class AvoidEntityGoalMC<T extends LivingEntity> extends Goal {
    protected final Starbuncle mob;
    private final double sprintSpeedModifier;
    protected T toAvoid;
    protected final float maxDist;
    protected Path path;
    protected final PathNavigation pathNav;
    protected final Class<T> avoidClass;
    protected final Predicate<LivingEntity> avoidPredicate;

    private final TargetingConditions avoidEntityTargeting;

    public AvoidEntityGoalMC(Starbuncle carby, Class<T> avoidClass, float maxDist, double sprintModifier) {
        this(carby, avoidClass, (p_200828_0_) -> true, maxDist, sprintModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }

    public AvoidEntityGoalMC(Starbuncle carby, Class<T> avoidClass, Predicate<LivingEntity> avoidPredicate, float maxDist, double sprintModifier, Predicate<LivingEntity> selectPredicate) {
        this.mob = carby;
        this.avoidClass = avoidClass;
        this.avoidPredicate = avoidPredicate;
        this.maxDist = maxDist;
        this.sprintSpeedModifier = sprintModifier;
        this.pathNav = carby.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.avoidEntityTargeting = (TargetingConditions.forCombat().range(maxDist).selector(selectPredicate.and(avoidPredicate)));
    }

    public boolean canUse() {
        this.toAvoid = this.mob.level.getNearestEntity(this.avoidClass, this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate(this.maxDist, 3.0D, this.maxDist));
        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3 vector3d = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
            if (vector3d == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vector3d.x, vector3d.y, vector3d.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.mob.getNavigation().tryMoveToBlockPos(BlockPos.containing(vector3d.x, vector3d.y, vector3d.z), sprintSpeedModifier);
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    public void start() {

    }

    public void stop() {
        this.toAvoid = null;
    }

    public void tick() {
    }
}