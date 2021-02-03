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
    public boolean shouldExecute() {
        return this.attacker instanceof WildenHunter && ((WildenHunter) this.attacker).ramCooldown <= 0 && super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.attacker instanceof WildenHunter && ((WildenHunter) this.attacker).ramCooldown <= 0 && super.shouldContinueExecuting();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if(distToEnemySqr - 5 <= d0)
            Networking.sendToNearby(attacker.world, attacker, new PacketAnimEntity(attacker.getEntityId(), WildenHunter.Animations.RAM.ordinal()));

        if (distToEnemySqr <= d0 && this.func_234041_j_() <= 0) {
            this.func_234039_g_();
            this.attacker.swingArm(Hand.MAIN_HAND);
            this.attacker.attackEntityAsMob(enemy);
            if(this.attacker instanceof WildenHunter)
                ((WildenHunter) this.attacker).ramCooldown = 200;
            enemy.applyKnockback(2.0F, enemy.getPosX() - attacker.getPosX(), enemy.getPosZ() - attacker.getPosZ());
            enemy.velocityChanged = true;

        }
    }
}
