package com.hollingsworth.arsnouveau.common.mixin.redstone;


import com.hollingsworth.arsnouveau.common.world.saved_data.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class RedstoneLevelMixin {

    @Inject(method = "getSignal", at = @At("RETURN"), cancellable = true)
    public void arsNouveau$getArsSignal(BlockGetter pLevel, BlockPos pPos, Direction pDirection, CallbackInfoReturnable<Integer> cir) {
        if(pLevel instanceof ServerLevel serverLevel) {
            RedstoneUtil.getArsSignal(serverLevel, pPos, pDirection, cir);
        }
    }
}
