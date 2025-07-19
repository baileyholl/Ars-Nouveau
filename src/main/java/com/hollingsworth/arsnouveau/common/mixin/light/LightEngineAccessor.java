package com.hollingsworth.arsnouveau.common.mixin.light;

import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LightEngine.class)
public interface LightEngineAccessor<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> {
    @Accessor
    S getStorage();
}
