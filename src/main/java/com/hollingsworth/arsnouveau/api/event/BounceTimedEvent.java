package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class BounceTimedEvent implements ITimedEvent{
    LivingEntity livingEntity;
    int duration;
    double oldY;
    public BounceTimedEvent(LivingEntity  e, double oldY){
        this.livingEntity = e;
        this.oldY = oldY;
    }

    @Override
    public void tick(boolean serverSide) {
        duration++;
        if(duration == 1){
            double f = 0.91d + 0.025d;
            Vector3d vec3d = this.livingEntity.getDeltaMovement();
            this.livingEntity.setDeltaMovement(vec3d.x / f, oldY, vec3d.z / f);
            this.livingEntity.hurtMarked = true;
        }
    }

    @Override
    public boolean isExpired() {
        return duration >= 1;
    }
}
