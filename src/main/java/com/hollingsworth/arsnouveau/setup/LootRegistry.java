package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.DungeonLootGenerator;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootRegistry {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ArsNouveau.MODID);

    public static final RegistryObject<Codec<DungeonLootGenerator.DungeonLootEnhancerModifier>> STRUCTURE_MODDED_LOOT_IMPORTER = GLM.register("dungeon_loot", DungeonLootGenerator.DungeonLootEnhancerModifier.CODEC);



}
