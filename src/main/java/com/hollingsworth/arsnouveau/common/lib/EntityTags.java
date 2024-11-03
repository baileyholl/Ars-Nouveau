package com.hollingsworth.arsnouveau.common.lib;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityTags {
    public static final TagKey<EntityType<?>> DRYGMY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "drygmy_blacklist"));
    public static final TagKey<EntityType<?>> DISINTEGRATION_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "disintegration_whitelist"));
    public static final TagKey<EntityType<?>> DISINTEGRATION_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "disintegration_blacklist"));
    public static final TagKey<EntityType<?>> HOSTILE_MOBS = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "an_hostile"));

    public static final TagKey<EntityType<?>> MAGIC_FIND = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "magic_find"));
    public static final TagKey<EntityType<?>> SPELL_CAN_HIT = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "spell_can_hit"));
    public static final TagKey<EntityType<?>> JAR_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "jar_blacklist"));
    public static final TagKey<EntityType<?>> INTERACT_JAR_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "interact_jar_blacklist"));
    public static final TagKey<EntityType<?>> JAR_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "jar_whitelist"));
    public static final TagKey<EntityType<?>> FAMILIAR = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "familiar"));
    public static final TagKey<EntityType<?>> LINGERING_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "lingering_blacklist"));
    public static final TagKey<EntityType<?>> BERRY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "berry_blacklist"));
    public static final TagKey<EntityType<?>> JAR_RELEASE_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "jar_release_blacklist"));
    public static final TagKey<EntityType<?>> ANIMAL_SUMMON_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "animal_summon_blacklist"));
    public static final TagKey<EntityType<?>> REWIND_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "rewind_blacklist"));

    public static final TagKey<EntityType<?>> ITEM_GRATE_PASSABLE = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "item_grate_passable"));
    public static final TagKey<EntityType<?>> ITEM_GRATE_COLLIDE = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "item_grate_collide"));

    public static final TagKey<EntityType<?>> BURST_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, ArsNouveau.prefix( "burst_whitelist"));

}
