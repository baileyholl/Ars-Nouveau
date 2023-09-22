package com.hollingsworth.arsnouveau.common.mixin;


import com.hollingsworth.arsnouveau.common.spell.effect.EffectRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class RedstoneLevelMixin {
    @Shadow
    public abstract ResourceKey<Level> dimension();

    @Inject(method = "getSignal", at = @At("RETURN"), cancellable = true)
    public void getArsSignal(BlockPos pPos, Direction pFacing, CallbackInfoReturnable<Integer> cir) {
        if (EffectRedstone.signalMap.get(dimension().location().toString()).containsKey(pPos.relative(pFacing.getOpposite()))) {
            cir.setReturnValue(Math.max(EffectRedstone.signalMap.get(dimension().location().toString()).get(pPos.relative(pFacing.getOpposite())), cir.getReturnValue()));
        }
    }

}
