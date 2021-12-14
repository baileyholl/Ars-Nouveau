package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class ElytraPlayerMixin {
//    @Redirect(
//            method = "tryToStartFallFlying",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/item/ItemStack;canElytraFly(Lnet/minecraft/entity/LivingEntity;)Z",
//                    remap = false
//            )
//    )
//    public boolean elytraOverride(ItemStack stack, LivingEntity entity) {
//        return entity.getEffect(ModPotions.GLIDE_EFFECT) != null || stack.canElytraFly(entity);
//    }
}
