package com.hollingsworth.arsnouveau.common.light;

import com.hollingsworth.arsnouveau.common.mixin.light.LayerLightSectionStorageAccessor;
import com.hollingsworth.arsnouveau.common.mixin.light.LevelLightEngineAccessor;
import com.hollingsworth.arsnouveau.common.mixin.light.LightEngineAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class SkyLightOverrider {
    public static void setSkyLight(Level level, BlockPos pos, int lightLevel) {
        if (level.isClientSide) {
            return;
        }

        if (!level.dimensionType().hasSkyLight()) {
            return;
        }

        if (level.getLightEngine() instanceof LevelLightEngineAccessor levelEngine) {
            if (levelEngine.getSkyEngine() instanceof LightEngineAccessor skyEngine) {
                if (skyEngine.getStorage() instanceof LayerLightSectionStorageAccessor storage) {
                    storage.set(pos.asLong(), lightLevel);
                }
                for (Direction d : Direction.values()) {
                    BlockPos offset = pos.relative(d);
                    level.getLightEngine().checkBlock(offset);
                }
            }
        }
    }
}
