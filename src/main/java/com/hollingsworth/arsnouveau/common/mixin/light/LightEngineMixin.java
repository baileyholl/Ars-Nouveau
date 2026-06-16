package com.hollingsworth.arsnouveau.common.mixin.light;

import com.hollingsworth.arsnouveau.common.light.ISkyLightSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public abstract class LightEngineMixin implements LightEngineAccessor {
    @Inject(method = "hasDifferentLightProperties", at = @At("HEAD"), cancellable = true)
    private static void an$beforeHasDifferentLightProperties(BlockGetter level, BlockPos pos, BlockState oldState, BlockState newState, CallbackInfoReturnable<Boolean> ci) {
        Block oldBlock = oldState.getBlock();
        Block newBlock = newState.getBlock();
        boolean oldEmitsSkyLight = false;
        boolean newEmitsSkyLight = false;
        if (oldBlock instanceof ISkyLightSource source) {
            oldEmitsSkyLight = source.emitsDirectSkyLight(oldState, level, pos);
        }
        if (newBlock instanceof ISkyLightSource source) {
            newEmitsSkyLight = source.emitsDirectSkyLight(newState, level, pos);
        }
        if (oldEmitsSkyLight != newEmitsSkyLight) {
            ci.setReturnValue(true);
        }
    }
}
