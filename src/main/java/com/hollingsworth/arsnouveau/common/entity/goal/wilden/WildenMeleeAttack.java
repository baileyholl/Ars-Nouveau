package com.hollingsworth.arsnouveau.common.entity.goal.wilden;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

public class WildenMeleeAttack extends MeleeAttackGoal {
    public int animArg;
    Supplier<Boolean> shouldExecute;
    public WildenMeleeAttack(CreatureEntity creature, double speedIn, boolean useLongMemory, int animArg, Supplier<Boolean> shouldExecute) {
        super(creature, speedIn, useLongMemory);
        this.animArg = animArg;
        this.shouldExecute = shouldExecute;
    }

    @Override
    public boolean shouldExecute() {
        return this.shouldExecute.get() && super.shouldExecute();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if(distToEnemySqr - 5 <= d0)
            Networking.sendToNearby(attacker.world, attacker, new PacketAnimEntity(attacker.getEntityId(), animArg));

        if (distToEnemySqr <= d0 && this.func_234041_j_() <= 0) {
            Networking.sendToNearby(attacker.world, attacker, new PacketAnimEntity(attacker.getEntityId(), animArg));
            this.func_234039_g_();
            this.attacker.swingArm(Hand.MAIN_HAND);
            this.attacker.attackEntityAsMob(enemy);
        }
    }
}
