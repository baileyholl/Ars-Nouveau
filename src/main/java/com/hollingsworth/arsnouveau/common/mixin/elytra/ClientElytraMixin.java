package com.hollingsworth.arsnouveau.common.mixin.elytra;


import com.hollingsworth.arsnouveau.common.spell.effect.EffectGlide;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class ClientElytraMixin {

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    public boolean elytraOverride(ItemStack stack, LivingEntity entity) {
        return EffectGlide.canGlide(entity) || stack.canElytraFly(entity);
    }
}