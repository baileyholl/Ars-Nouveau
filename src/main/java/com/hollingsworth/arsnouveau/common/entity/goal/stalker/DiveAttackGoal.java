package com.hollingsworth.arsnouveau.common.entity.goal.stalker;

import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DiveAttackGoal extends Goal {
    WildenStalker stalker;

    public DiveAttackGoal(WildenStalker stalker) {
        this.stalker = stalker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        return stalker.getTarget() != null && stalker.isFlying();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        LivingEntity livingentity = stalker.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!(livingentity instanceof Player player) || !livingentity.isSpectator() && !player.isCreative()) {
            return this.canUse();
        } else {
            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        //Networking.sendToNearby(stalker.world, stalker, new PacketAnimEntity(stalker.getEntityId(), WildenStalker.Animations.DIVE.ordinal()));
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        LivingEntity livingentity = stalker.getTarget();
        stalker.orbitOffset = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
        if (stalker.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
            stalker.doHurtTarget(livingentity);
            stalker.setFlying(false);
        } else if (stalker.horizontalCollision || stalker.hurtTime > 0) {
            stalker.setFlying(false);
        }

    }
}
