package com.hollingsworth.arsnouveau.common.mixin.elytra;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectGlide;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Redirect(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    public boolean elytraOverride(ItemStack stack, LivingEntity entity) {
        return EffectGlide.canGlide(entity) || stack.canElytraFly(entity);
    }

    @Redirect(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;elytraFlightTick(Lnet/minecraft/world/entity/LivingEntity;I)Z",
                    remap = false
            )
    )
    public boolean eytraValidOverride(ItemStack stack, LivingEntity entity, int flightTicks) {
        return EffectGlide.canGlide(entity) || stack.elytraFlightTick(entity, flightTicks);
    }
}
