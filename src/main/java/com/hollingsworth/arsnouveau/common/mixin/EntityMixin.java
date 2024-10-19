package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyReturnValue(method = "isInWaterOrRain", at = @At("RETURN"))
    private boolean ars_nouveau$isInWaterOrRain(boolean original) {
        if (((Entity) (Object) this) instanceof LivingEntity livingEntity && isWet(livingEntity)) {
            return true;
        }
        return original;
    }

    @Inject(method = "setRemainingFireTicks", at = @At("HEAD"), cancellable = true)
    private void ars_nouveau$setSecondsOnFire(int pRemainingFireTicks, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof LivingEntity livingEntity && isWet(livingEntity) && pRemainingFireTicks > 0) {
            livingEntity.clearFire();
            ci.cancel();
        }
    }


    private static boolean isWet(LivingEntity livingEntity){
        return livingEntity.hasEffect(ModPotions.SOAKED_EFFECT) || livingEntity.getVehicle() instanceof BubbleEntity;
    }
}
