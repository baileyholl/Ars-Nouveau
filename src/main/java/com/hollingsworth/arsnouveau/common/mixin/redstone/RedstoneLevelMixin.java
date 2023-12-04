package com.hollingsworth.arsnouveau.common.mixin.redstone;


import com.hollingsworth.arsnouveau.common.world.saved_data.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class RedstoneLevelMixin {
    @Shadow
    public abstract ResourceKey<Level> dimension();

    @Shadow public abstract boolean setBlock(BlockPos pPos, BlockState pNewState, int pFlags);

    @Shadow public abstract boolean setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft);

    @Inject(method = "getSignal", at = @At("RETURN"), cancellable = true)
    public void getArsSignal(BlockPos pPos, Direction pFacing, CallbackInfoReturnable<Integer> cir) {
        Level level = (Level) (Object) this;
        if(level instanceof ServerLevel serverLevel) {
            RedstoneUtil.getArsSignal(serverLevel, pPos, pFacing, cir);
        }
    }

}
