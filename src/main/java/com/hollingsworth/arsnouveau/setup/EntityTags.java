package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;

public class EntityTags {
    public static final Tag.Named<EntityType<?>> DRYGMY_BLACKLIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "drygmy_blacklist"));
    public static final Tag.Named<EntityType<?>> DISINTEGRATION_WHITELIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_whitelist"));
    public static final Tag.Named<EntityType<?>> DISINTEGRATION_BLACKLIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_blacklist"));
}
