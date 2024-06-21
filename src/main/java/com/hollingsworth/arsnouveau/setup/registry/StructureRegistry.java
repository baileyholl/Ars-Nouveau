package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.world.processors.WaterloggingFixProcessor;
import com.hollingsworth.arsnouveau.common.world.structure.WildenDen;
import com.hollingsworth.arsnouveau.common.world.structure.WildenGuardianDen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class StructureRegistry {
    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, ArsNouveau.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<WildenDen>> WILDEN_DEN = STRUCTURES.register("wilden_den", () -> () -> WildenDen.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<WildenGuardianDen>> WILDEN_GUARDIAN_DEN = STRUCTURES.register("wilden_guardian_den", () -> () -> WildenGuardianDen.CODEC);

    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, ArsNouveau.MODID);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WaterloggingFixProcessor>> WATERLOGGING_FIX_PROCESSOR = STRUCTURE_PROCESSOR.register("waterlogging_fix", () -> () -> WaterloggingFixProcessor.CODEC);
}
