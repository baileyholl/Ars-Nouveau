package com.hollingsworth.arsnouveau.common.mixin.light;

import net.minecraft.core.Direction;
import net.minecraft.world.level.lighting.SkyLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SkyLightEngine.class)
public interface SkyLightEngineAccessor extends LightEngineAccessor {
    @Invoker
    void callUpdateSourcesInColumn(int columnX, int columnZ, int surfaceY);

    @Invoker
    void callRemoveSourcesBelow(int columnX, int columnZ, int surfaceY, int minSectionY);

    @Invoker
    int callGetLowestSourceY(int columnX, int columnZ, int fallback);
}
