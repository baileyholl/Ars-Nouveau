package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootEnhancerModifier;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class LootRegistry {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ArsNouveau.MODID);

    public static final RegistryObject<Codec<DungeonLootEnhancerModifier>> STRUCTURE_MODDED_LOOT_IMPORTER = GLM.register("dungeon_loot", DungeonLootEnhancerModifier.CODEC);



}
