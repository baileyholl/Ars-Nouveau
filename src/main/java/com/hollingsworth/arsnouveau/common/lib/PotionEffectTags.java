package com.hollingsworth.arsnouveau.common.lib;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class PotionEffectTags {
    private static final HashMap<TagKey<MobEffect>, ArrayList<Holder<MobEffect>>> potionEffects = new HashMap<>();

    public static final TagKey<MobEffect> UNSTABLE_GIFTS = TagKey.create(Registries.MOB_EFFECT, ArsNouveau.prefix("unstable_gifts"));
    public static final TagKey<MobEffect> DISPEL_DENY = TagKey.create(Registries.MOB_EFFECT, ArsNouveau.prefix("deny_dispel"));
    public static final TagKey<MobEffect> DISPEL_ALLOW = TagKey.create(Registries.MOB_EFFECT, ArsNouveau.prefix("allow_dispel"));
    public static final TagKey<MobEffect> TO_SYNC = TagKey.create(Registries.MOB_EFFECT, ArsNouveau.prefix("to_sync"));

    public static ArrayList<Holder<MobEffect>> getEffects(Level level, TagKey<MobEffect> tag) {
        return potionEffects.computeIfAbsent(tag, (_key) -> {
            Optional<HolderSet.Named<MobEffect>> effects = level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTag(tag);
            if (effects.isEmpty()) return null;
            ArrayList<Holder<MobEffect>> effectList = new ArrayList<>();
            for (Holder<MobEffect> mobEffectHolder : effects.get()) {
                effectList.add(mobEffectHolder);
            }
            return effectList;
        });
    }
}
