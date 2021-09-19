package com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker;

import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class SmashGoal extends MeleeAttackGoal {

    public SmashGoal(CreatureEntity entity, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(entity, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null && super.canUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if(distToEnemySqr - 5 <= d0)
            Networking.sendToNearby(mob.level, mob, new PacketAnimEntity(mob.getId(), WealdWalker.Animations.SMASH.ordinal()));

        if (distToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
            Networking.sendToNearby(mob.level, mob, new PacketAnimEntity(mob.getId(), WealdWalker.Animations.SMASH.ordinal()));
            for(LivingEntity e : mob.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(mob.blockPosition()).inflate(3.0))){
                if(e instanceof WealdWalker)
                    continue;

                if(e instanceof PlayerEntity && mob.getTarget() instanceof PlayerEntity){
                    this.mob.doHurtTarget(e);
                }else if(e instanceof MonsterEntity || e.equals(mob.getTarget())){
                    this.mob.doHurtTarget(e);
                }
                e.knockback(2, MathHelper.sin(mob.yRot * ((float)Math.PI / 180F)), -MathHelper.cos(mob.yRot * ((float)Math.PI / 180F)));
            }
            this.resetAttackCooldown();
            this.mob.swing(Hand.MAIN_HAND);
            this.mob.doHurtTarget(enemy);
        }
    }
}
