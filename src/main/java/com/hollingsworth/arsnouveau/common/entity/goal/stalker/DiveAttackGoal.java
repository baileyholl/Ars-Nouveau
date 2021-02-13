package com.hollingsworth.arsnouveau.common.entity.goal.stalker;

import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class DiveAttackGoal extends Goal {
    WildenStalker stalker;
    public DiveAttackGoal(WildenStalker stalker) {
        this.stalker = stalker;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        return stalker.getAttackTarget() != null && stalker.isFlying();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        LivingEntity livingentity = stalker.getAttackTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!(livingentity instanceof PlayerEntity) || !((PlayerEntity)livingentity).isSpectator() && !((PlayerEntity)livingentity).isCreative()) {
            return this.shouldExecute();
        } else {
            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        //Networking.sendToNearby(stalker.world, stalker, new PacketAnimEntity(stalker.getEntityId(), WildenStalker.Animations.DIVE.ordinal()));
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        LivingEntity livingentity = stalker.getAttackTarget();
        stalker.orbitOffset = new Vector3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5D), livingentity.getPosZ());
        if (stalker.getBoundingBox().grow((double)0.2F).intersects(livingentity.getBoundingBox())) {
            stalker.attackEntityAsMob(livingentity);
            stalker.setFlying(false);
//            if (!PhantomEntity.this.isSilent()) {
//                PhantomEntity.this.world.playEvent(1039, PhantomEntity.this.getPosition(), 0);
//            }
        } else if (stalker.collidedHorizontally || stalker.hurtTime > 0) {
            stalker.setFlying(false);
        }

    }
}
