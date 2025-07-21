package com.hollingsworth.arsnouveau.common.mixin.light;

import com.hollingsworth.arsnouveau.common.light.ISkyLightSource;
import com.hollingsworth.arsnouveau.common.light.SkyLightOverrider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.SkyLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyLightEngine.class)
public abstract class SkyLightEngineMixin implements SkyLightEngineAccessor {
    private SkyLightOverrider an$overrider = null;

    private SkyLightOverrider an$getOverrider() {
        if (an$overrider != null) {
            return an$overrider;
        }
        an$overrider = SkyLightOverrider.forLevel((Level) getChunkSource().getLevel());
        return an$overrider;
    }

    @Inject(method = "propagateDecrease", at = @At("HEAD"), cancellable = true)
    private void an$beforePropagateDecrease(long packedPos, long flags, CallbackInfo ci) {
        BlockPos pos = BlockPos.of(packedPos);
        BlockState blockState = callGetState(pos);
        Block block = blockState.getBlock();

        if (block instanceof ISkyLightSource source) {
            if (source.emitsDirectSkyLight(blockState, getChunkSource().getLevel(), pos)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "checkNode", at = @At("HEAD"), cancellable = true)
    private void an$beforeCheckNode(long packedPos, CallbackInfo ci) {
        if (an$getOverrider().beforeCheckNode(this, BlockPos.of(packedPos))) {
            ci.cancel();
        }
    }

    @Inject(method = "removeSourcesBelow", at = @At("HEAD"), cancellable = true)
    private void an$beforeRemoveSourcesBelow(int x, int z, int surfaceY, int minValidY, CallbackInfo ci) {
        if (surfaceY <= minValidY) {
            return;
        }
        int y = surfaceY - 1;
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = callGetState(pos);
        Block block = blockState.getBlock();

        if (block instanceof ISkyLightSource source) {
            if (source.emitsDirectSkyLight(blockState, getChunkSource().getLevel(), pos)) {
                ci.cancel();
            }
        }
    }
}
