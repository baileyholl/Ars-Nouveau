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
    protected final Predicate<LivingEntity> predicateOnAvoidEntity;
    private final EntityPredicate avoidEntityTargeting;

    public AvoidEntityGoalMC(EntityCarbuncle p_i46404_1_, Class<T> p_i46404_2_, float p_i46404_3_, double p_i46404_4_, double p_i46404_6_) {
        this(p_i46404_1_, p_i46404_2_, (p_200828_0_) -> {
            return true;
        }, p_i46404_3_, p_i46404_4_, p_i46404_6_, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test);
    }

    public AvoidEntityGoalMC(EntityCarbuncle p_i48859_1_, Class<T> p_i48859_2_, Predicate<LivingEntity> p_i48859_3_, float p_i48859_4_, double p_i48859_5_, double p_i48859_7_, Predicate<LivingEntity> p_i48859_9_) {
        this.mob = p_i48859_1_;
        this.avoidClass = p_i48859_2_;
        this.avoidPredicate = p_i48859_3_;
        this.maxDist = p_i48859_4_;
        this.walkSpeedModifier = p_i48859_5_;
        this.sprintSpeedModifier = p_i48859_7_;
        this.predicateOnAvoidEntity = p_i48859_9_;
        this.pathNav = p_i48859_1_.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.avoidEntityTargeting = (new EntityPredicate()).range((double)p_i48859_4_).selector(p_i48859_9_.and(p_i48859_3_));
    }

    public AvoidEntityGoalMC(EntityCarbuncle p_i48860_1_, Class<T> p_i48860_2_, float p_i48860_3_, double p_i48860_4_, double p_i48860_6_, Predicate<LivingEntity> p_i48860_8_) {
        this(p_i48860_1_, p_i48860_2_, (p_203782_0_) -> {
            return true;
        }, p_i48860_3_, p_i48860_4_, p_i48860_6_, p_i48860_8_);
    }

    public boolean canUse() {
        this.toAvoid = this.mob.level.getNearestLoadedEntity(this.avoidClass, this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0D, (double)this.maxDist));
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