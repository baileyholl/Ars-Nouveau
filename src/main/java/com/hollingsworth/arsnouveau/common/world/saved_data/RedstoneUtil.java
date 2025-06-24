package com.hollingsworth.arsnouveau.common.world.saved_data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


public class RedstoneUtil {

    public static void getArsSignal(ServerLevel serverLevel, BlockPos pPos, Direction pFacing, CallbackInfoReturnable<Integer> cir) {
        var map = RedstoneSavedData.from(serverLevel).SIGNAL_MAP;
        if (map.isEmpty()) {
            return;
        }

        var entry = map.get(pPos);
        if (entry != null) {
            cir.setReturnValue(Math.max(entry.power, cir.getReturnValue()));
        } else if (map.containsKey(pPos.relative(pFacing))) {
            cir.setReturnValue(Math.max(map.get(pPos.relative(pFacing)).power, cir.getReturnValue()));
        } else if (map.containsKey(pPos.relative(pFacing.getOpposite()))) {
            cir.setReturnValue(Math.max(map.get(pPos.relative(pFacing.getOpposite())).power, cir.getReturnValue()));
        }
    }
}
