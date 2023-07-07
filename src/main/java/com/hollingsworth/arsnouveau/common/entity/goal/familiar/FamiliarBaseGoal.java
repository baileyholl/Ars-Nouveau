package com.hollingsworth.arsnouveau.common.entity.goal.familiar;

import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class FamiliarBaseGoal extends Goal {
    public FamiliarEntity entity;

    public FamiliarBaseGoal(FamiliarEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
