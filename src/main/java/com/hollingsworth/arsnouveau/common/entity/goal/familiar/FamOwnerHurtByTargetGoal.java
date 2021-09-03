package com.hollingsworth.arsnouveau.common.entity.goal.familiar;

import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

public class FamOwnerHurtByTargetGoal extends TargetGoal {

    IFamiliar familiar;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public FamOwnerHurtByTargetGoal(IFamiliar familiar){
        super((MobEntity) familiar.getThisEntity(), false);
        this.familiar = familiar;
    }

    @Override
    public boolean canUse() {
        Entity owner = this.familiar.getOwnerServerside();
        LivingEntity livingentity = owner instanceof LivingEntity ? (LivingEntity) owner : null;

        if (livingentity == null) {
            return false;
        } else {
            this.ownerLastHurtBy = livingentity.getLastHurtByMob();
            int i = livingentity.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, EntityPredicate.DEFAULT) && this.familiar.wantsToAttack(this.ownerLastHurtBy, livingentity);
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingentity = (LivingEntity) this.familiar.getOwnerServerside();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
