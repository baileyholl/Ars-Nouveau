package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import net.minecraft.entity.ai.goal.Goal;

public class ChimeraSpikeGoal extends Goal {

    EntityChimera boss;

    public ChimeraSpikeGoal(EntityChimera boss){
        this.boss = boss;
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
