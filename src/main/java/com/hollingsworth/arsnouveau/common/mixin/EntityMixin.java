package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyReturnValue(method = "isInWaterOrRain", at = @At("RETURN"))
    private boolean ars_nouveau$isInWaterOrRain(boolean original) {
        if (((Entity) (Object) this) instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModPotions.SOAKED_EFFECT.get())) {
            return true;
        }
        return original;
    }

    @ModifyReturnValue(method = "setSecondsOnFire", at = @At("RETURN"))
    private int ars_nouveau$setSecondsOnFire(int original) {
        if (((Entity) (Object) this) instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModPotions.SOAKED_EFFECT.get())) {
            return 0;
        }
        return original;
    }

}
