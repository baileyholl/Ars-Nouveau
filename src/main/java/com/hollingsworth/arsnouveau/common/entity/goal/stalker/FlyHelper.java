package com.hollingsworth.arsnouveau.common.entity.goal.stalker;

import com.hollingsworth.arsnouveau.common.entity.WildenStalker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class FlyHelper extends MoveControl {

    private float speedFactor = 0.4F;

    public FlyHelper(WildenStalker entityIn) {
        super(entityIn);
    }

    public void tick() {
        WildenStalker mob = (WildenStalker) this.mob;
        if(!mob.isFlying()){
            super.tick();
            return;
        }

        if (mob.horizontalCollision) {
            mob.yRot += 180.0F;

        }

        float f = (float)(mob.orbitOffset.x - mob.getX());
        float f1 = (float)(mob.orbitOffset.y - mob.getY());
        float f2 = (float)(mob.orbitOffset.z - mob.getZ());
        double d0 = Mth.sqrt(f * f + f2 * f2);
        double d1 = 1.0D - (double)Mth.abs(f1 * 0.7F) / d0;
        f = (float)((double)f * d1);
        f2 = (float)((double)f2 * d1);
        d0 = Mth.sqrt(f * f + f2 * f2);
        double d2 = Mth.sqrt(f * f + f2 * f2 + f1 * f1);
        float f3 = mob.yRot;
        float f4 = (float)Mth.atan2(f2, f);
        float f5 = Mth.wrapDegrees(mob.yRot + 90.0F);
        float f6 = Mth.wrapDegrees(f4 * (180F / (float) Math.PI));
        mob.yRot = Mth.approachDegrees(f5, f6, 4.0F) - 90.0F;
        mob.yBodyRot = mob.yRot;
        if (Mth.degreesDifferenceAbs(f3, mob.yRot) < 3.0F) {
            this.speedFactor = Mth.approach(this.speedFactor, 1.8F, 0.005F * (1.8F / this.speedFactor));
        } else {
            this.speedFactor = Mth.approach(this.speedFactor, 0.2F, 0.025F);
        }

        float f7 = (float) (-(Mth.atan2(-f1, d0) * (double) (180F / (float) Math.PI)));
        mob.setXRot(f7);
        float f8 = mob.getYRot() + 90.0F;
        double d3 = (double) (this.speedFactor * Mth.cos(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f / d2);
        double d4 = (double) (this.speedFactor * Mth.sin(f8 * ((float) Math.PI / 180F))) * Math.abs((double) f2 / d2);
        double d5 = (double) (this.speedFactor * Mth.sin(f7 * ((float) Math.PI / 180F))) * Math.abs((double) f1 / d2);
        Vec3 vector3d = mob.getDeltaMovement();
        mob.setDeltaMovement(vector3d.add((new Vec3(d3, d5, d4)).subtract(vector3d).scale(0.2D)));
    }

}
