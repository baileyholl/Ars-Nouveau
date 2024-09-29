package com.hollingsworth.arsnouveau.common.entity.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Supplier;

public class LookAtTarget extends Goal {

    protected final Mob mob;
    protected final float lookDistance;
    Supplier<Vec3> lookAtPosition;


    public LookAtTarget(Mob pMob,  float pLookDistance, Supplier<Vec3> pLookAtPosition) {
        this.mob = pMob;
        this.lookDistance = pLookDistance;
        this.lookAtPosition = pLookAtPosition;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public void tick() {
        super.tick();
        mob.getLookControl().setLookAt(lookAtPosition.get());
    }

    @Override
    public boolean canUse() {
        return this.lookAtPosition.get() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.lookAtPosition.get() != null && !(this.mob.distanceToSqr(this.lookAtPosition.get()) > (double) (this.lookDistance * this.lookDistance));
    }
}
