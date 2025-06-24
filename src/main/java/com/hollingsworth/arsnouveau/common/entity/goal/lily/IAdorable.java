package com.hollingsworth.arsnouveau.common.entity.goal.lily;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface IAdorable {

    private LivingEntity getThis() {
        return (LivingEntity) this;
    }

    default boolean isLookingAtMe(Player pPlayer) {
        Vec3 vec3 = pPlayer.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(getThis().getX() - pPlayer.getX(), getThis().getEyeY() - pPlayer.getEyeY(), getThis().getZ() - pPlayer.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1.0D - 0.025D / d0 && pPlayer.hasLineOfSight(getThis());
    }


    void setWagging(boolean wagging);

    void setWagTicks(int ticks);
}
