package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.ITEMS;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArsNouveau.MODID);
    public static final RegistryObject<CreativeModeTab> BLOCKS = TABS.register("general", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ars_nouveau"))
            .icon(() -> ItemsRegistry.CREATIVE_SPELLBOOK.get().getDefaultInstance())
            .displayItems((params, output) -> {
                for (RegistryObject<Item> entry : ITEMS.getEntries()) {
                    if (!(entry.get() instanceof Glyph)) {
                        output.accept(entry.get().getDefaultInstance());
                    }
                }
                for (PerkItem perk : PerkRegistry.getPerkItemMap().values()) {
                    output.accept(perk.getDefaultInstance());
                }
                for (RitualTablet ritual : RitualRegistry.getRitualItemMap().values()) {
                    output.accept(ritual.getDefaultInstance());
                }
                for (FamiliarScript familiar : FamiliarRegistry.getFamiliarScriptMap().values()) {
                    output.accept(familiar.getDefaultInstance());
                }

            }).withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .build());

    public static final RegistryObject<CreativeModeTab> GLYPHS = TABS.register("glyphs", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ars_glyphs"))
            .icon(() -> MethodProjectile.INSTANCE.glyphItem.getDefaultInstance())
            .displayItems((params, output) -> {

                for (var glyph : GlyphRegistry.getSpellpartMap().values().stream()
                        .sorted(BaseBook.COMPARE_TYPE_THEN_NAME).toList()) {
                    output.accept(glyph.getGlyph().getDefaultInstance());
                }

            }).withTabsBefore(BLOCKS.getKey())
            .build());
}
