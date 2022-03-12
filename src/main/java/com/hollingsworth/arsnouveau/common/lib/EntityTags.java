package com.hollingsworth.arsnouveau.common.lib;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;

public class EntityTags {
    public static final Tag.Named<EntityType<?>> DRYGMY_BLACKLIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "drygmy_blacklist"));
    public static final Tag.Named<EntityType<?>> DISINTEGRATION_WHITELIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_whitelist"));
    public static final Tag.Named<EntityType<?>> DISINTEGRATION_BLACKLIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_blacklist"));

    public static final Tag.Named<EntityType<?>> MAGIC_FIND = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "magic_find"));
    public static final Tag.Named<EntityType<?>> SPELL_CAN_HIT = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "spell_can_hit"));
}
