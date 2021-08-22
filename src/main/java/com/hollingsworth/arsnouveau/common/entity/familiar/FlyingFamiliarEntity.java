package com.hollingsworth.arsnouveau.common.entity.familiar;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;

public class FlyingFamiliarEntity extends FamiliarEntity{
    public FlyingFamiliarEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
        this.moveControl = new FlyingMovementController(this, 10, true);
    }

    @Override
    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public boolean canTeleport() {
        return true;
    }

    @Override
    protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
        return 0;
    }

    @Override
    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }
}
