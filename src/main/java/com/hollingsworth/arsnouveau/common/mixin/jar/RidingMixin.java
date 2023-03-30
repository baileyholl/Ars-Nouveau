package com.hollingsworth.arsnouveau.common.mixin.jar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class RidingMixin {
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "HEAD"), cancellable = true)
    private void canRide(Entity pVehicle, boolean pForce, CallbackInfoReturnable<Boolean> cir) {
        if (pVehicle.getLevel().getBlockEntity(pVehicle.getOnPos()) instanceof MobJarTile) {
            cir.setReturnValue(false);
        }
    }
}
