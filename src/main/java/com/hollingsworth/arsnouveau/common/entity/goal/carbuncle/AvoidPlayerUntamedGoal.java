package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.item.Items;

public class AvoidPlayerUntamedGoal extends AvoidEntityGoal<LivingEntity> {
    EntityCarbuncle carbuncle;
    public AvoidPlayerUntamedGoal(EntityCarbuncle entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
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
