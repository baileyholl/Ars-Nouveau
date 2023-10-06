package com.hollingsworth.arsnouveau.common.lib;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class PotionEffectTags {
    private static final HashMap<TagKey<MobEffect>, ArrayList<MobEffect>> potionEffects = new HashMap<>();
    public static final TagKey<MobEffect> UNSTABLE_GIFTS = TagKey.create(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "unstable_gifts"));
    public static final TagKey<MobEffect> DISPEL_DENY = TagKey.create(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "deny_dispel"));
    public static final TagKey<MobEffect> DISPEL_ALLOW = TagKey.create(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "allow_dispel"));

    public static ArrayList<MobEffect> getEffects(TagKey<MobEffect> tag) {
        return potionEffects.computeIfAbsent(tag, (_key) -> {
            Optional<HolderSet.Named<MobEffect>> effects = Registry.MOB_EFFECT.getTag(tag);
            if (effects.isEmpty()) return null;

            ArrayList<MobEffect> effectList = new ArrayList<>();
            for (Holder<MobEffect> mobEffectHolder : effects.get()) {
                effectList.add(mobEffectHolder.get());
            }
            return effectList;
        });
    }
}
