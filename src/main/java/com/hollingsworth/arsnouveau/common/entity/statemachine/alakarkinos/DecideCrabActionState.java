package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import org.jetbrains.annotations.Nullable;

public class DecideCrabActionState extends CrabState {
    public DecideCrabActionState(Alakarkinos alakarkinos) {
        super(alakarkinos);
    }

    @Override
    public @Nullable CrabState tick() {
        if(!alakarkinos.tamed || alakarkinos.getEntityData().get(Alakarkinos.HOME).isEmpty()){
            return null;
        }

        if(!alakarkinos.getEntityData().get(Alakarkinos.HAS_HAT)){
            return new GetHatState(alakarkinos);
        }

        if(alakarkinos.findBlockCooldown <= 0){
            return new FindBlockState(alakarkinos);
        }

        return super.tick();
    }
}
