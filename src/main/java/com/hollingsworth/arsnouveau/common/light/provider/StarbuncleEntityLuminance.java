package com.hollingsworth.arsnouveau.common.light.provider;

import com.hollingsworth.arsnouveau.common.light.LambDynamicLightsInitializer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record StarbuncleEntityLuminance(int trigger, int luminance) implements EntityLuminance {
    public static final MapCodec<StarbuncleEntityLuminance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Brightness.LIGHT_VALUE_CODEC.fieldOf("trigger").forGetter(StarbuncleEntityLuminance::trigger),
            Brightness.LIGHT_VALUE_CODEC.fieldOf("luminance").forGetter(StarbuncleEntityLuminance::luminance)
    ).apply(instance, StarbuncleEntityLuminance::new));

    @Override
    public @NotNull Type type() {
        return LambDynamicLightsInitializer.STARBUNCLE;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
        if (entity.level().getBrightness(LightLayer.BLOCK, entity.blockPosition()) < this.trigger) {
            return this.luminance;
        }
        return 0;
    }
}
