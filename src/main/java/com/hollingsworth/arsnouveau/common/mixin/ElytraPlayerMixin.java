package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class ElytraPlayerMixin {
    @Redirect(
            method = "tryToStartFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;canElytraFly(Lnet/minecraft/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    public boolean elytraOverride(ItemStack stack, LivingEntity entity) {
        return entity.getEffect(ModPotions.GLIDE_EFFECT) != null || stack.canElytraFly(entity);
    }
}
