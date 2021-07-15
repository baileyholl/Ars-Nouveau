package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import net.minecraft.entity.ai.goal.Goal;

public class ChimeraRamGoal extends Goal {
    EntityChimera boss;

    public ChimeraRamGoal(EntityChimera boss){
        this.boss = boss;
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
