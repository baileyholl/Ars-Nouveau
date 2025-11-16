package com.hollingsworth.arsnouveau.common.entity.goal.familiar;

import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

public class FamOwnerHurtByTargetGoal extends TargetGoal {

    IFamiliar familiar;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public FamOwnerHurtByTargetGoal(IFamiliar familiar) {
        super((Mob) familiar.getThisEntity(), false);
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
            return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.familiar.wantsToAttack(this.ownerLastHurtBy, livingentity);
        }
    }

    @Override
    protected boolean canAttack(@Nullable LivingEntity potentialTarget, TargetingConditions targetPredicate) {
        if (potentialTarget == null) {
            return false;
        }
        return !potentialTarget.getUUID().equals(familiar.getOwnerID()) && super.canAttack(potentialTarget, targetPredicate);
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
