package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;


public class DamageTypesRegistry {

    public static final ResourceKey<DamageType> GENERIC_SPELL_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ArsNouveau.prefix("spell"));
    public static final ResourceKey<DamageType> WINDSHEAR = ResourceKey.create(Registries.DAMAGE_TYPE, ArsNouveau.prefix("windshear"));
    public static final ResourceKey<DamageType> COLD_SNAP = ResourceKey.create(Registries.DAMAGE_TYPE, ArsNouveau.prefix("frost"));
    public static final ResourceKey<DamageType> FLARE = ResourceKey.create(Registries.DAMAGE_TYPE, ArsNouveau.prefix("flare"));
    public static final ResourceKey<DamageType> CRUSH = ResourceKey.create(Registries.DAMAGE_TYPE, ArsNouveau.prefix("crush"));
    public static final ResourceKey<DamageType> SOURCE_BERRY_BUSH = ResourceKey.create(Registries.DAMAGE_TYPE, ArsNouveau.prefix(LibBlockNames.SOURCEBERRY_BUSH));

}
