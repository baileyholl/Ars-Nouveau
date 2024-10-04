package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.loot.DrygmyLootCondition;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootEnhancerModifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LootRegistry {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ArsNouveau.MODID);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<DungeonLootEnhancerModifier>> STRUCTURE_MODDED_LOOT_IMPORTER = GLM.register("dungeon_loot", () -> DungeonLootEnhancerModifier.CODEC);

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ArsNouveau.MODID);

    public static final Supplier<LootItemConditionType> IS_DRYGMY =
            LOOT_CONDITION_TYPES.register("is_drygmy", () -> new LootItemConditionType(DrygmyLootCondition.CODEC));
}
