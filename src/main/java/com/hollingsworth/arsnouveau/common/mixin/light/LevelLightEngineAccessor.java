package com.hollingsworth.arsnouveau.common.mixin.light;

import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelLightEngine.class)
public interface LevelLightEngineAccessor {
    @Accessor
    LightEngine<?, ?> getSkyEngine();
}
