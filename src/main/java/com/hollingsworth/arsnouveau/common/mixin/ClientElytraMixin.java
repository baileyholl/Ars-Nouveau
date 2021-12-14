package com.hollingsworth.arsnouveau.common.mixin;


import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public class ClientElytraMixin {

//    @Redirect(
//            method = "aiStep",
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