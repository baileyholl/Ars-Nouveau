package com.hollingsworth.arsnouveau.common.entity.goal;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.function.Predicate;

public class AvoidEntityGoalMC<T extends LivingEntity> extends Goal {
    protected final EntityCarbuncle mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    protected T toAvoid;
    protected final float maxDist;
    protected Path path;
    protected final PathNavigator pathNav;
    protected final Class<T> avoidClass;
    protected final Predicate<LivingEntity> avoidPredicate;

    private final EntityPredicate avoidEntityTargeting;

    public AvoidEntityGoalMC(EntityCarbuncle carby, Class<T> avoidClass, float maxDist, double walkModifier, double sprintModifier) {
        this(carby, avoidClass, (p_200828_0_) -> true, maxDist, walkModifier, sprintModifier, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test);
    }

    public AvoidEntityGoalMC(EntityCarbuncle carby, Class<T> avoidClass, Predicate<LivingEntity> avoidPredicate, float maxDist, double walkModifier, double sprintModifier, Predicate<LivingEntity> selectPredicate) {
        this.mob = carby;
        this.avoidClass = avoidClass;
        this.avoidPredicate = avoidPredicate;
        this.maxDist = maxDist;
        this.walkSpeedModifier = walkModifier;
        this.sprintSpeedModifier = sprintModifier;
        this.pathNav = carby.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.avoidEntityTargeting = (new EntityPredicate()).range(maxDist).selector(selectPredicate.and(avoidPredicate));
    }

    public boolean canUse() {
        this.toAvoid = this.mob.level.getNearestLoadedEntity(this.avoidClass, this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate(this.maxDist, 3.0D, this.maxDist));
        if (this.toAvoid == null) {
            return false;
        } else {
            Vector3d vector3d = RandomPositionGenerator.getPosAvoid(this.mob, 16, 7, this.toAvoid.position());
            if (vector3d == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vector3d.x, vector3d.y, vector3d.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.mob.getNavigation().tryMoveToBlockPos(new BlockPos(vector3d.x, vector3d.y, vector3d.z), sprintSpeedModifier);
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
//        if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
//            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
//        } else {
//            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
//        }

    }
}