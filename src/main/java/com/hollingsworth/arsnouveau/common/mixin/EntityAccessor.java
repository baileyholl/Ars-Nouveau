package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    int getBoardingCooldown();

    @Accessor
    void setBoardingCooldown(int boardingCooldown);
}
