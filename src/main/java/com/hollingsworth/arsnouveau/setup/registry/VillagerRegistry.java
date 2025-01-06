package com.hollingsworth.arsnouveau.setup.registry;

import com.google.common.collect.ImmutableSet;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VillagerRegistry {

    static String ARS_TRADER = "shady_wizard";

    public static final DeferredRegister<VillagerProfession> VILLAGERS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, ArsNouveau.MODID);
    public static final DeferredRegister<PoiType> POIs = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, ArsNouveau.MODID);

    public static final DeferredHolder<PoiType, PoiType> ARCANE_POI = POIs.register("arcane_poi", () -> new PoiType(ImmutableSet.copyOf(BlockRegistry.ARCANE_CORE_BLOCK.get().getStateDefinition().getPossibleStates()),1,1));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> SHARDS_TRADER = VILLAGERS.register(ARS_TRADER, () -> new VillagerProfession(ARS_TRADER, (x) -> x.value() == ARCANE_POI.get(), (x) -> x.value() == ARCANE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC));

}
