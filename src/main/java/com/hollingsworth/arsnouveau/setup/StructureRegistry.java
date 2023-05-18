package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;

public class StructureRegistry {
    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, ArsNouveau.MODID);

//    public static final RegistryObject<StructureType<WildenDen>> WILDEN_DEN = STRUCTURES.register("wilden_den", () -> () -> WildenDen.CODEC);

}
