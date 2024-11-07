package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.setup.config.Config;
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

        if(alakarkinos.needSource() && alakarkinos.level.getGameTime() % 20 == 0){
            var result = SourceUtil.takeSourceWithParticles(alakarkinos.getHome(), alakarkinos.blockPosition().above(), alakarkinos.level, 5, Config.ALAKARKINOS_SOURCE_COST.get());

            if(result != null ){
                alakarkinos.setNeedSource(false);
            }
        }

        if(!alakarkinos.needSource() && alakarkinos.findBlockCooldown <= 0){
            return new FindBlockState(alakarkinos);
        }

        return super.tick();
    }
}
