package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface ExpInvokerMixin {

    @Invoker("getExperienceReward")
    int an_getExperienceReward(Player p_70693_1_);

    @Invoker("shouldDropExperience")
    boolean an_shouldDropExperience();

}
