package com.hollingsworth.arsnouveau.common.entity.goal.familiar;

import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FamOwnerHurtTargetGoal extends TargetGoal {
    IFamiliar familiar;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public FamOwnerHurtTargetGoal(IFamiliar familiar) {
        super((Mob) familiar.getThisEntity(), false);
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
            return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.familiar.wantsToAttack(this.ownerLastHurt, livingentity);
        }
    }

    @Override
    protected boolean canAttack(@Nullable LivingEntity potentialTarget, TargetingConditions targetPredicate) {
        if (potentialTarget == null) {
            return false;
        }
        return !potentialTarget.getUUID().equals(familiar.getOwnerID()) && super.canAttack(potentialTarget, targetPredicate);
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
