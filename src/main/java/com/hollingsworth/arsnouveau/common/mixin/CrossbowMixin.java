package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.items.SpellCrossbow;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {
    @Inject(method= "performShooting(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;FF)V",
    at= @At("HEAD"),
            cancellable = true)
    private static void arsNouveau$performShooting(Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand, ItemStack pCrossbowStack, float pVelocity, float pInaccuracy, CallbackInfo ci) {
        if(!(pShooter instanceof Player) && pCrossbowStack.getItem() instanceof SpellCrossbow spellCrossbow){
            spellCrossbow.shootStoredProjectiles(pLevel, pShooter, pUsedHand, pCrossbowStack, pVelocity, pInaccuracy);
            ci.cancel();
        }
    }
}
