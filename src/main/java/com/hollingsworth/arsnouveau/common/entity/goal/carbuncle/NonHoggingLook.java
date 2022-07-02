package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class NonHoggingLook extends LookAtPlayerGoal {
    Starbuncle carbuncle;

    public NonHoggingLook(Starbuncle entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn) {
        super(entityIn, watchTargetClass, maxDistance, chanceIn);
        carbuncle = entityIn;
    }

}
