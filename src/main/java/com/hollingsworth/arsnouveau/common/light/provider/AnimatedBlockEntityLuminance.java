package com.hollingsworth.arsnouveau.common.light.provider;

import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.hollingsworth.arsnouveau.common.light.LambDynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class AnimatedBlockEntityLuminance implements EntityLuminance {
    public static final AnimatedBlockEntityLuminance INSTANCE = new AnimatedBlockEntityLuminance();

    private AnimatedBlockEntityLuminance() {
    }

    @Override
    public @NotNull Type type() {
        return LambDynamicLightsInitializer.ANIMATED_BLOCK;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
        if (entity instanceof AnimBlockSummon animatedBlock) {
            return animatedBlock.blockState.getLightEmission(entity.level(), entity.blockPosition());
        }

        return 0;
    }
}
