package com.hollingsworth.arsnouveau.common.world.saved_data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class RedstoneUtil {

    public static void getArsSignal(ServerLevel serverLevel, BlockPos pPos, Direction pFacing, CallbackInfoReturnable<Integer> cir) {
        BlockPos facing = pPos.relative(pFacing);
        BlockPos requestingPos = pPos.relative(pFacing.getOpposite());
        var map = RedstoneSavedData.from(serverLevel).SIGNAL_MAP;
        var entry = map.get(pPos);
        if(entry != null){
            cir.setReturnValue(Math.max(entry.power, cir.getReturnValue()));
        }else if(map.containsKey(facing)){
            cir.setReturnValue(Math.max(map.get(facing).power, cir.getReturnValue()));
        }else if(map.containsKey(requestingPos)){
            cir.setReturnValue(Math.max(map.get(requestingPos).power, cir.getReturnValue()));
        }
    }
}
