package com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker;

import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import com.hollingsworth.arsnouveau.common.entity.goal.AnimatedAttackGoal;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

import java.util.function.Supplier;

public class SmashGoal extends AnimatedAttackGoal {

    WealdWalker walker;

    public SmashGoal(WealdWalker entity, boolean followUnseen, Supplier<Boolean> canAttack, int animationID, int animationLength, int attackRange) {
        super(entity, followUnseen, canAttack, animationID, animationLength, attackRange, 1.2f);
        this.walker = entity;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !(mob.getTarget() instanceof Creeper);
    }

    @Override
    public void stop() {
        super.stop();
        walker.getEntityData().set(WealdWalker.SMASHING, false);
    }

    @Override
    protected void attack(LivingEntity target) {
        super.attack(target);
        target.knockback(1.2F, Mth.sin(walker.yRot * ((float) Math.PI / 180F)), -Mth.cos(walker.yRot * ((float) Math.PI / 180F)));
        walker.smashCooldown = 60;
    }

    @Override
    public void onArrive() {
        super.onArrive();
        walker.getEntityData().set(WealdWalker.SMASHING, true);
    }

    @Override
    public void look(LivingEntity entity) {
    }
}
