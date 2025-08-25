package com.hollingsworth.arsnouveau.common.light;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.light.provider.*;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;

public class LambDynamicLightsInitializer implements DynamicLightsInitializer {
    public static final EntityLuminance.Type ANIMATED_BLOCK = EntityLuminance.Type.registerSimple(
            ArsNouveau.prefix("animated_block"), AnimatedBlockEntityLuminance.INSTANCE
    );
    public static final EntityLuminance.Type ENCHANTED_FALLING_BLOCK = EntityLuminance.Type.registerSimple(
            ArsNouveau.prefix("enchanted_falling_block"), EnchantedFallingBlockEntityLuminance.INSTANCE
    );
    public static final EntityLuminance.Type ENCHANTED_MAGE_BLOCK = EntityLuminance.Type.registerSimple(
            ArsNouveau.prefix("enchanted_mage_block"), EnchantedMageBlockEntityLuminance.INSTANCE
    );
    public static final EntityLuminance.Type JAR_OF_LIGHT = EntityLuminance.Type.registerSimple(
            ArsNouveau.prefix("jar_of_light"), JarOfLightEntityLuminance.INSTANCE
    );
    public static final EntityLuminance.Type STARBUNCLE = EntityLuminance.Type.register(
            ArsNouveau.prefix("starbuncle"), StarbuncleEntityLuminance.CODEC
    );

    @SuppressWarnings({"removal", "UnstableApiUsage"})
    @Override
    public void onInitializeDynamicLights(ItemLightSourceManager itemLightSourceManager) {
        // Note: required for backwards compatibility with LDL v3.
        LightManager.isLambDynamicLightsPresent = true;
    }
}
