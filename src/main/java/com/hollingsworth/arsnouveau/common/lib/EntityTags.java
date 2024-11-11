package com.hollingsworth.arsnouveau.common.lib;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityTags {
    public static final TagKey<EntityType<?>> DRYGMY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "drygmy_blacklist"));
    public static final TagKey<EntityType<?>> DISINTEGRATION_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "disintegration_whitelist"));
    public static final TagKey<EntityType<?>> DISINTEGRATION_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "disintegration_blacklist"));
    public static final TagKey<EntityType<?>> HOSTILE_MOBS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "an_hostile"));

    public static final TagKey<EntityType<?>> MAGIC_FIND = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "magic_find"));
    public static final TagKey<EntityType<?>> SPELL_CAN_HIT = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "spell_can_hit"));
    public static final TagKey<EntityType<?>> JAR_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "jar_blacklist"));
    public static final TagKey<EntityType<?>> INTERACT_JAR_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "interact_jar_blacklist"));
    public static final TagKey<EntityType<?>> JAR_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "jar_whitelist"));
    public static final TagKey<EntityType<?>> FAMILIAR = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "familiar"));
    public static final TagKey<EntityType<?>> LINGERING_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "lingering_blacklist"));
    public static final TagKey<EntityType<?>> BERRY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "berry_blacklist"));
    public static final TagKey<EntityType<?>> JAR_RELEASE_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "jar_release_blacklist"));
    public static final TagKey<EntityType<?>> ANIMAL_SUMMON_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "animal_summon_blacklist"));
    public static final TagKey<EntityType<?>> REWIND_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "rewind_blacklist"));
    public static final TagKey<EntityType<?>> BURST_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ArsNouveau.MODID, "burst_whitelist"));
}
