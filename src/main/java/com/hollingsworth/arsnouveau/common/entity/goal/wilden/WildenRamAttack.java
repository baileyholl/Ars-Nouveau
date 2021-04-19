package com.hollingsworth.arsnouveau.common.entity.goal.wilden;

import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

public class WildenRamAttack extends MeleeAttackGoal {
    public WildenRamAttack(CreatureEntity creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    public boolean canUse() {
        return this.mob instanceof WildenHunter && ((WildenHunter) this.mob).ramCooldown <= 0 && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob instanceof WildenHunter && ((WildenHunter) this.mob).ramCooldown <= 0 && super.canContinueToUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if(distToEnemySqr - 5 <= d0)
            Networking.sendToNearby(mob.level, mob, new PacketAnimEntity(mob.getId(), WildenHunter.Animations.RAM.ordinal()));

        if (distToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(Hand.MAIN_HAND);
            this.mob.doHurtTarget(enemy);
            if(this.mob instanceof WildenHunter)
                ((WildenHunter) this.mob).ramCooldown = 200;
            enemy.knockback(2.0F, enemy.getX() - mob.getX(), enemy.getZ() - mob.getZ());
            enemy.hurtMarked = true;

        }
    }
}
