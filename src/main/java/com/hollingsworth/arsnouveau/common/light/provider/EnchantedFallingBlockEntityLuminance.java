package com.hollingsworth.arsnouveau.common.light.provider;

import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.light.LambDynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class EnchantedFallingBlockEntityLuminance implements EntityLuminance {
    public static final EnchantedFallingBlockEntityLuminance INSTANCE = new EnchantedFallingBlockEntityLuminance();

    private EnchantedFallingBlockEntityLuminance() {
    }

    @Override
    public @NotNull Type type() {
        return LambDynamicLightsInitializer.ENCHANTED_FALLING_BLOCK;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
        if (entity instanceof EnchantedFallingBlock enchantedFallingBlock) {
            return enchantedFallingBlock.blockState.getLightEmission(entity.level(), entity.blockPosition());
        }

        return 0;
    }
}
