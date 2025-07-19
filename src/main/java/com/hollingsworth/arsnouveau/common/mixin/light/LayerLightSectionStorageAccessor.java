package com.hollingsworth.arsnouveau.common.mixin.light;

import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LayerLightSectionStorage.class)
public interface LayerLightSectionStorageAccessor {
    @Invoker("setStoredLevel")
    void set(long packedPos, int lightLevel);
}
