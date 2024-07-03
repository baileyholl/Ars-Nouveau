package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.items.data.*;
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

    public static final DeferredHolder<DataComponentType<?> , DataComponentType<ArmorPerkHolder>> ARMOR_PERKS = DATA.register("armor_perks", () -> DataComponentType.<ArmorPerkHolder>builder().persistent(ArmorPerkHolder.CODEC).networkSynchronized(ArmorPerkHolder.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?> , DataComponentType<DominionWandData>> DOMINION_WAND = DATA.register("dominion_wand", () -> DataComponentType.<DominionWandData>builder().persistent(DominionWandData.CODEC).networkSynchronized(DominionWandData.STREAM).build());

    public static final DeferredHolder<DataComponentType<?> , DataComponentType<PresentData>> PRESENT = DATA.register("present", () -> DataComponentType.<PresentData>builder().persistent(PresentData.CODEC).networkSynchronized(PresentData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?> , DataComponentType<ScryData>> SCRY_DATA = DATA.register("scry_data", () -> DataComponentType.<ScryData>builder().persistent(ScryData.CODEC).networkSynchronized(ScryData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?> , DataComponentType<WarpScrollData>> WARP_SCROLL = DATA.register("warp_scroll", () -> DataComponentType.<WarpScrollData>builder().persistent(WarpScrollData.CODEC).networkSynchronized(WarpScrollData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MultiPotionContents>> MULTI_POTION = DATA.register("multi_potion", () -> DataComponentType.<MultiPotionContents>builder().persistent(MultiPotionContents.CODEC).networkSynchronized(MultiPotionContents.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LightJarData>> LIGHT_JAR = DATA.register("light_jar", () -> DataComponentType.<LightJarData>builder().persistent(LightJarData.CODEC).networkSynchronized(LightJarData.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CodexData>> CODEX_DATA = DATA.register("codex_data", () -> DataComponentType.<CodexData>builder().persistent(CodexData.CODEC).networkSynchronized(CodexData.STREAM).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpellCaster>> SPELL_CASTER = DATA.register("spell_caster", () -> DataComponentType.<SpellCaster>builder().persistent(SpellCaster.CODEC.codec()).networkSynchronized(SpellCaster.STREAM).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionLauncherData>> POTION_LAUNCHER = DATA.register("potion_launcher", () -> DataComponentType.<PotionLauncherData>builder().persistent(PotionLauncherData.CODEC.codec()).networkSynchronized(PotionLauncherData.STREAM).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockFillContents>> BLOCK_FILL_CONTENTS = DATA.register("block_fill_contents", () -> DataComponentType.<BlockFillContents>builder().persistent(BlockFillContents.CODEC).networkSynchronized(BlockFillContents.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MobJarData>> MOB_JAR = DATA.register("mob_jar", () -> DataComponentType.<MobJarData>builder().persistent(MobJarData.CODEC).networkSynchronized(MobJarData.STREAM).build());
}
