package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArsNouveau.MODID);
    public static final RegistryObject<CreativeModeTab> BLOCKS = TABS.register("general", () -> CreativeModeTab.builder()
            .icon(() -> ItemsRegistry.CREATIVE_SPELLBOOK.get().getDefaultInstance())
            .build());

    public static final RegistryObject<CreativeModeTab> GLYPHS = TABS.register("glyphs", () -> CreativeModeTab.builder()
            .icon(() -> ItemsRegistry.CREATIVE_SPELLBOOK.get().getDefaultInstance())
            .build());
}
