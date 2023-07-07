package com.hollingsworth.arsnouveau.common.entity.goal.wealdwalker;

import com.hollingsworth.arsnouveau.common.entity.WealdWalker;

import java.util.function.Supplier;

public class CastSpellGoal extends CastGoal<WealdWalker> {
    WealdWalker walker;

    public CastSpellGoal(WealdWalker entity, double speed, float attackRange, Supplier<Boolean> canUse, int animId, int delayTicks) {
        super(entity, speed, attackRange, canUse, animId, delayTicks);
        this.walker = entity;
    }

    @Override
    public void start() {
        super.start();
        walker.getEntityData().set(WealdWalker.CASTING, true);
    }

    @Override
    public void stop() {
        super.stop();
        walker.getEntityData().set(WealdWalker.CASTING, false);
    }
}
