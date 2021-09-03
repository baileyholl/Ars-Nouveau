package com.hollingsworth.arsnouveau.common.entity.goal.familiar;

import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;

public class FamOwnerHurtTargetGoal extends TargetGoal {
    IFamiliar familiar;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public FamOwnerHurtTargetGoal(IFamiliar familiar){
        super((MobEntity) familiar.getThisEntity(), false);
        this.familiar = familiar;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        Entity owner = this.familiar.getOwnerServerside();
        LivingEntity livingentity = owner instanceof LivingEntity ? (LivingEntity) owner : null;
        if (livingentity == null) {
            return false;
        } else {
            this.ownerLastHurt = livingentity.getLastHurtMob();
            int i = livingentity.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurt, EntityPredicate.DEFAULT) && this.familiar.wantsToAttack(this.ownerLastHurt, livingentity);
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingentity = (LivingEntity) this.familiar.getOwnerServerside();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
