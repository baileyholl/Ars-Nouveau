package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;


public class DamageTypesRegistry {

    public static final ResourceKey<DamageType> WINDSHEAR = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ArsNouveau.MODID, "windshear"));
    public static final ResourceKey<DamageType> COLD_SNAP = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ArsNouveau.MODID, "frost"));
    public static final ResourceKey<DamageType> FLARE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ArsNouveau.MODID, "flare"));
    public static final ResourceKey<DamageType> CRUSH = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ArsNouveau.MODID, "crush"));


}
