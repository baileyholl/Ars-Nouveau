package com.hollingsworth.arsnouveau.common.light.provider;

import com.hollingsworth.arsnouveau.common.entity.EnchantedMageblock;
import com.hollingsworth.arsnouveau.common.light.LambDynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class EnchantedMageBlockEntityLuminance implements EntityLuminance {
    public static final EnchantedMageBlockEntityLuminance INSTANCE = new EnchantedMageBlockEntityLuminance();

    private EnchantedMageBlockEntityLuminance() {
    }

    @Override
    public @NotNull Type type() {
        return LambDynamicLightsInitializer.ENCHANTED_MAGE_BLOCK;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
        if (entity instanceof EnchantedMageblock enchantedMageblock) {
            return enchantedMageblock.blockState.getLightEmission(entity.level(), entity.blockPosition());
        }

        return 0;
    }
}
