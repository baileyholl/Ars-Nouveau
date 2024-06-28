package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.familiar.PersistentFamiliarData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class DataComponentRegistry {

    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PersistentFamiliarData>> PERSISTENT_FAMILIAR_DATA = DATA.register("persistent_familiar_data", () -> DataComponentType.<PersistentFamiliarData>builder().persistent(PersistentFamiliarData.CODEC.codec()).networkSynchronized(PersistentFamiliarData.STREAM_CODEC).build());
}
