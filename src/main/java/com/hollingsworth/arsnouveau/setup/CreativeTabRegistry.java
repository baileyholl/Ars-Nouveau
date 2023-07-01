package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArsNouveau.MODID);
    public static final RegistryObject<CreativeModeTab> BLOCKS = TABS.register("general", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ars_nouveau"))
            .icon(() -> ItemsRegistry.CREATIVE_SPELLBOOK.get().getDefaultInstance())
            .displayItems((params, output) -> {
                for (Map.Entry<ResourceKey<Item>, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
                    if (entry.getKey().location().getNamespace().equals(ArsNouveau.MODID)
                        && !(entry.getValue() instanceof Glyph)) {
                        output.accept(entry.getValue().getDefaultInstance());
                    }
                }
            }).withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .build());

    public static final RegistryObject<CreativeModeTab> GLYPHS = TABS.register("glyphs", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ars_glyphs"))
            .icon(() -> ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE).getDefaultInstance())
            .displayItems((params, output) -> {

                for (var glyph : ArsNouveauAPI.getInstance().getSpellpartMap().values().stream()
                        .sorted(BaseBook.COMPARE_TYPE_THEN_NAME).toList()) {
                    output.accept(ArsNouveauAPI.getInstance().getGlyphItem(glyph).getDefaultInstance());
                }

            }).withTabsBefore(BLOCKS.getKey())
            .build());
}
