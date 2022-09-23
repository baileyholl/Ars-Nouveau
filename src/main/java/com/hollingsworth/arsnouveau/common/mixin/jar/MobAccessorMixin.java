package com.hollingsworth.arsnouveau.common.mixin.jar;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface MobAccessorMixin {

    @Invoker
    SoundEvent callGetAmbientSound();
}
