package com.hollingsworth.arsnouveau.common.light.provider;

import com.hollingsworth.arsnouveau.common.light.DynamLightUtil;
import com.hollingsworth.arsnouveau.common.light.LambDynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public final class JarOfLightEntityLuminance implements EntityLuminance {
    public static final JarOfLightEntityLuminance INSTANCE = new JarOfLightEntityLuminance();

    private JarOfLightEntityLuminance() {
    }

    @Override
    public @NotNull Type type() {
        return LambDynamicLightsInitializer.JAR_OF_LIGHT;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
        if (entity instanceof Player p) {
            return DynamLightUtil.getJarOfLightLuminance(p);
        }

        return 0;
    }
}
