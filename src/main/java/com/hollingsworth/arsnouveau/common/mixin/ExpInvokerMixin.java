package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface ExpInvokerMixin {

    @Invoker("getExperienceReward")
    int an_getExperienceReward(PlayerEntity p_70693_1_);

    @Invoker("shouldDropExperience")
    boolean an_shouldDropExperience();

}
