package com.hollingsworth.arsnouveau.common.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapMethod(method = "getGravity")
    public double wrapGravity(Operation<Double> original) {
        return original.call();
    }
}
