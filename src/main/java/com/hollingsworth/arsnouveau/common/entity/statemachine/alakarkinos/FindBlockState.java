package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FindBlockState extends CrabState{
    public FindBlockState(Alakarkinos alakarkinos) {
        super(alakarkinos);
    }

    @Override
    public @Nullable CrabState tick() {

        var pos = alakarkinos.getHome();
        if(pos == null){
            return new DecideCrabActionState(alakarkinos);
        }

        var radius = 8;
        for(BlockPos pos1 : BlockPos.withinManhattan(pos, radius, 3, radius)){
            var state = alakarkinos.level.getBlockState(pos1);
            var consumable = state.is(BlockTagProvider.ALAKARKINOS_GRAVEL) || state.is(BlockTagProvider.ALAKARKINOS_SAND);
            if(!consumable)
                continue;
            var path = alakarkinos.getNavigation().createPath(pos1, 2);
            if(path == null || !path.canReach()){
                continue;
            }
            return new PlaceHatState(alakarkinos, PlaceHatState.findHatPos(alakarkinos), pos1);
        }

        return new DecideCrabActionState(alakarkinos);
    }
}
