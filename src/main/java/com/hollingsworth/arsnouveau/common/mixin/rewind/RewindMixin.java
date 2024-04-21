package com.hollingsworth.arsnouveau.common.mixin.rewind;

import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class RewindMixin {

    @WrapWithCondition(method = "setDeltaMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private boolean shouldUpdateMovement(Entity entity, Vec3 vec3) {
        System.out.println("checking should update");
        return !(this instanceof IRewindable rewindable) || !rewindable.isRewinding();
    }

    @WrapWithCondition(method = "setDeltaMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"))
    private boolean shouldUpdateMovement(Entity entity, double x, double y, double z) {
        System.out.println("update doubles");
        return !(this instanceof IRewindable rewindable) || !rewindable.isRewinding();
    }
}
