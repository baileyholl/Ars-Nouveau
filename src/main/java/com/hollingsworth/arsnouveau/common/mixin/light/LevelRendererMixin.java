package com.hollingsworth.arsnouveau.common.mixin.light;

import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(
            method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At("TAIL"),
            cancellable = true
    )
    private static void ars_nouveau$onGetLightmapCoordinates(BlockAndTintGetter world, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!LightManager.shouldUpdateDynamicLight())
            return; // Do not touch to the value.
        if (!world.getBlockState(pos).isSolidRender(world, pos))
            cir.setReturnValue(LightManager.getLightmapWithDynamicLight(pos, cir.getReturnValue()));
    }
}
