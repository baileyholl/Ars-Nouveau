package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.ITEMS;

public class CreativeTabRegistry {
    public static Comparator<RecipeHolder<GlyphRecipe>> COMPARE_GLYPH_BY_TYPE = Comparator.comparingInt(holder -> holder.value().getSpellPart().getTypeIndex());

    public static Comparator<RecipeHolder<GlyphRecipe>> COMPARE_TYPE_THEN_NAME = COMPARE_GLYPH_BY_TYPE.thenComparing(holder -> holder.value().getSpellPart().getLocaleName());
    public static Comparator<RecipeHolder<GlyphRecipe>> COMPARE_TIER_THEN_NAME = COMPARE_GLYPH_BY_TYPE.thenComparingInt(o -> o.value().getSpellPart().getConfigTier().value).thenComparing(holder -> holder.value().getSpellPart().getLocaleName());

    public static Comparator<AbstractSpellPart> COMPARE_SPELL_TYPE_NAME = Comparator.comparingInt(AbstractSpellPart::getTypeIndex).thenComparing(AbstractSpellPart::getLocaleName);


    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArsNouveau.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS = TABS.register("general", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ars_nouveau"))
            .icon(() -> ItemsRegistry.CREATIVE_SPELLBOOK.get().getDefaultInstance())
            .displayItems((params, output) -> {
                for (DeferredHolder<Item, ? extends Item> entry : ITEMS.getEntries()) {
                    if (!(entry.get() instanceof Glyph)) {
                        output.accept(entry.get().getDefaultInstance());
                    }
                }
                for (PerkItem perk : PerkRegistry.PERK_ITEMS) {
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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GLYPHS = TABS.register("glyphs", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ars_glyphs"))
            .icon(() -> MethodProjectile.INSTANCE.glyphItem.getDefaultInstance())
            .displayItems((params, output) -> {

                for (var glyph : ANRegistries.GLYPH_TYPES.stream()
                        .sorted(COMPARE_SPELL_TYPE_NAME).toList()) {
                    output.accept(glyph.getGlyph().getDefaultInstance());
                }

            }).withTabsBefore(BLOCKS.getKey())
            .build());


}
