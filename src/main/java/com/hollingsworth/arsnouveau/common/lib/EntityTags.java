package com.hollingsworth.arsnouveau.common.lib;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityTags {
    public static final TagKey<EntityType<?>> DRYGMY_BLACKLIST = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "drygmy_blacklist"));
    public static final TagKey<EntityType<?>> DISINTEGRATION_WHITELIST =  TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "disintegration_whitelist"));
    public static final TagKey<EntityType<?>> DISINTEGRATION_BLACKLIST =  TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "disintegration_blacklist"));
    public static final TagKey<EntityType<?>> HOSTILE_MOBS =  TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "an_hostile"));

    public static final TagKey<EntityType<?>> MAGIC_FIND =  TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "magic_find"));
    public static final TagKey<EntityType<?>> SPELL_CAN_HIT = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ArsNouveau.MODID, "spell_can_hit"));
}
