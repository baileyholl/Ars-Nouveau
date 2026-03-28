package com.hollingsworth.arsnouveau.common.entity.familiar;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class FlyingFamiliarEntity extends FamiliarEntity {
    public FlyingFamiliarEntity(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level world) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        // 1.21.11: setCanPassDoors removed from FlyingPathNavigation
        return flyingpathnavigator;
    }

    @Override
    public boolean canTeleport() {
        return true;
    }

    // 1.21.11: calculateFallDamage(float, float) → calculateFallDamage(double, float)
    @Override
    protected int calculateFallDamage(double p_225508_1_, float p_225508_2_) {
        return 0;
    }

    // 1.21.11: causeFallDamage(float, float, DamageSource) → causeFallDamage(double, float, DamageSource)
    @Override
    public boolean causeFallDamage(double p_147187_, float p_147188_, @NotNull DamageSource p_147189_) {
        return false;
    }
}
