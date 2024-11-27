package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.*;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class Documentation {

    public static void initOnWorldReload(){
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();

        for(AbstractSpellPart spellPart : GlyphRegistry.getSpellpartMap().values()){
            ItemStack renderStack = spellPart.glyphItem.getDefaultInstance();
            var entry = new DocEntry(spellPart.getRegistryName(), renderStack, Component.literal(spellPart.getLocaleName()));
            entry.addPage(GlyphEntry.create(spellPart));

            var pages = getRecipePages(renderStack, spellPart.getRegistryName());
            entry.addPages(pages);

            DocumentationRegistry.registerEntry(glyphCategory(spellPart.getConfigTier()), entry);
        }


        for (RitualTablet r : RitualRegistry.getRitualItemMap().values()) {
            ItemStack renderStack = r.getDefaultInstance();
            AbstractRitual ritual = r.ritual;

            Component title = Component.translatable("item." + ritual.getRegistryName().getNamespace() + "." + ritual.getRegistryName().getPath());

            var entry = new DocEntry(ritual.getRegistryName(), renderStack,title);

            entry.addPage(TextEntry.create(Component.translatable(ritual.getDescriptionKey()), title, renderStack));

            List<SinglePageCtor> pages = getRecipePages(renderStack, ritual.getRegistryName());
            entry.addPages(pages);

            DocumentationRegistry.registerEntry(DocumentationRegistry.RITUAL_INDEX, entry);
        }

    }

    public static List<SinglePageCtor> getRecipePages(ItemStack stack, ResourceLocation recipeId){
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();

        List<SinglePageCtor> pages = new ArrayList<>();

        RecipeHolder<GlyphRecipe> glyphRecipe = manager.byKeyTyped(RecipeRegistry.GLYPH_TYPE.get(), recipeId);

        if(glyphRecipe != null){
            pages.add(GlyphRecipeEntry.create(glyphRecipe));
            return pages;
        }

        RecipeHolder<CraftingRecipe> recipe = manager.byKeyTyped(RecipeType.CRAFTING, recipeId);

        if(recipe != null){
            pages.add(CraftingEntry.create(recipe));
            return pages;
        }

        RecipeHolder<EnchantingApparatusRecipe> apparatusRecipe = manager.byKeyTyped(RecipeRegistry.APPARATUS_TYPE.get(), recipeId);

        if(apparatusRecipe != null){
            pages.add(ApparatusEntry.create(apparatusRecipe));
            return pages;
        }

        return pages;
    }


    public static DocCategory glyphCategory(SpellTier tier){
        return switch (tier.value) {
            case 1 -> DocumentationRegistry.GLYPH_TIER_ONE;
            case 2 -> DocumentationRegistry.GLYPH_TIER_TWO;
            case 3, 99 -> DocumentationRegistry.GLYPH_TIER_THREE;
            default -> DocumentationRegistry.GLYPH_TIER_ONE;
        };
    }
}
