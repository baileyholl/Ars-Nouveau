package com.hollingsworth.arsnouveau.setup;

import com.google.common.collect.ImmutableSet;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VillagerRegistry {

    static String ARS_TRADER = "disguised_starbuncle";



    public static final DeferredRegister<VillagerProfession> VILLAGERS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, ArsNouveau.MODID);

    public static final DeferredRegister<PoiType> POIs = DeferredRegister.create(ForgeRegistries.POI_TYPES, ArsNouveau.MODID);

    public static final RegistryObject<PoiType> ARCANE_POI = POIs.register("arcane_poi", () -> new PoiType(ImmutableSet.copyOf(BlockRegistry.ARCANE_CORE_BLOCK.getStateDefinition().getPossibleStates()),1,1));
    public static final RegistryObject<VillagerProfession> SHARDS_TRADER = VILLAGERS.register(ARS_TRADER, () -> new VillagerProfession(ARS_TRADER, (x) -> x.get() == ARCANE_POI.get(), (x) -> x.get() == ARCANE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_SHEPHERD));

}
