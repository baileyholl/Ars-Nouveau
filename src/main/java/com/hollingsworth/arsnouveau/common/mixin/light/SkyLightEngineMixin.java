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
    private SkyLightOverrider overrider = null;

    private SkyLightOverrider getOverrider() {
        if (overrider != null) {
            return overrider;
        }
        overrider = SkyLightOverrider.forLevel((Level) getChunkSource().getLevel());
        return overrider;
    }

    @Inject(method = "propagateDecrease", at = @At("HEAD"), cancellable = true)
    private void beforePropagateDecrease(long packedPos, long flags, CallbackInfo ci) {
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
    private void beforeCheckNode(long packedPos, CallbackInfo ci) {
        if (getOverrider().beforeCheckNode(this, BlockPos.of(packedPos))) {
            ci.cancel();
        }
    }
}
