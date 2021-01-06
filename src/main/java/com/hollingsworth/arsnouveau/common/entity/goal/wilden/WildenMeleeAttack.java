package com.hollingsworth.arsnouveau.common.entity.goal.wilden;

import com.hollingsworth.arsnouveau.common.entity.WildenHunter;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

public class WildenMeleeAttack extends MeleeAttackGoal {
    public WildenMeleeAttack(CreatureEntity creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if(distToEnemySqr - 2 <= d0)
            Networking.sendToNearby(attacker.world, attacker, new PacketAnimEntity(attacker.getEntityId(), WildenHunter.Animations.ATTACK.ordinal()));

        if (distToEnemySqr <= d0 && this.func_234041_j_() <= 0) {
            this.func_234039_g_();
            this.attacker.swingArm(Hand.MAIN_HAND);
            System.out.println("sending packet");
            this.attacker.attackEntityAsMob(enemy);
        }
    }
}
