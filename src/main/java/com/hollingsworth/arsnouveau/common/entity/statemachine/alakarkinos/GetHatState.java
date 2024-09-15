package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GetHatState extends CrabState{
    BlockPos target;
    public GetHatState(Alakarkinos alakarkinos) {
        super(alakarkinos);
        target = alakarkinos.hatPos;
    }

    @Override
    public @Nullable CrabState tick() {
        if(target == null){
            alakarkinos.hatPos = null;
            alakarkinos.getEntityData().set(Alakarkinos.HAS_HAT, true);
            return new DecideCrabActionState(alakarkinos);
        }
        BlockState hatState = alakarkinos.level.getBlockState(alakarkinos.hatPos);
        if(hatState.getBlock() != BlockRegistry.CRAB_HAT.get()){
            alakarkinos.hatPos = null;
            alakarkinos.getEntityData().set(Alakarkinos.HAS_HAT, true);
            return new DecideCrabActionState(alakarkinos);
        }else{
            if(BlockUtil.distanceFrom(alakarkinos.blockPosition(), alakarkinos.hatPos) <= 3) {
                alakarkinos.hatPos = null;
                alakarkinos.getEntityData().set(Alakarkinos.HAS_HAT, true);
                alakarkinos.level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
                return new DecideCrabActionState(alakarkinos);
            }else{
                alakarkinos.getNavigation().moveTo(alakarkinos.hatPos.getX(), alakarkinos.hatPos.getY(), alakarkinos.hatPos.getZ(), 1.0);
            }
        }
        return super.tick();
    }
}
