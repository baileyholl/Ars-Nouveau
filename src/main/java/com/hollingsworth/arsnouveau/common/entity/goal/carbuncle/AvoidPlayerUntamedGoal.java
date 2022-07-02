package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.item.Items;

public class AvoidPlayerUntamedGoal extends AvoidEntityGoal<LivingEntity> {
    Starbuncle carbuncle;

    public AvoidPlayerUntamedGoal(Starbuncle entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn, (living) -> living.getMainHandItem().getItem() != Items.GOLD_NUGGET);
        this.carbuncle = entityIn;
    }

    @Override
    public boolean canUse() {
        if (carbuncle.isTamed())
            return false;
        return super.canUse();
    }
}
