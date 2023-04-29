package com.hollingsworth.arsnouveau.common.lib;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class PotionEffectTags {
    public static final TagKey<MobEffect> UNSTABLE_GIFTS = TagKey.create(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "unstable_gifts"));
}
