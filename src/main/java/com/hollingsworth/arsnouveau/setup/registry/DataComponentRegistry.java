package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.common.items.data.ItemScrollData;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.common.items.data.VoidJarData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class DataComponentRegistry {

    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PersistentFamiliarData>> PERSISTENT_FAMILIAR_DATA = DATA.register("persistent_familiar_data", () -> DataComponentType.<PersistentFamiliarData>builder().persistent(PersistentFamiliarData.CODEC.codec()).networkSynchronized(PersistentFamiliarData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StarbuncleCharmData>> STARBUNCLE_DATA = DATA.register("starbuncle_data", () -> DataComponentType.<StarbuncleCharmData>builder().persistent(StarbuncleCharmData.CODEC.codec()).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemScrollData>> ITEM_SCROLL_DATA = DATA.register("item_scroll_data", () -> DataComponentType.<ItemScrollData>builder().persistent(ItemScrollData.CODEC).networkSynchronized(ItemScrollData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?> , DataComponentType<VoidJarData>> VOID_JAR = DATA.register("void_jar", () -> DataComponentType.<VoidJarData>builder().persistent(VoidJarData.CODEC).networkSynchronized(VoidJarData.STREAM_CODEC).build());
}
