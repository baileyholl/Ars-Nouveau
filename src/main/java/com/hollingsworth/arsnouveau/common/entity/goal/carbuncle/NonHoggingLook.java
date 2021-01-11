package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;

public class NonHoggingLook extends LookAtGoal {
    EntityCarbuncle carbuncle;
    public NonHoggingLook(EntityCarbuncle entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance, float chanceIn) {
        super(entityIn, watchTargetClass, maxDistance, chanceIn);
        carbuncle = entityIn;
    }

}
