package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.*;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class Documentation {

    public static void initOnWorldReload(){
        Level level = ArsNouveau.proxy.getClientWorld();
        RecipeManager manager = level.getRecipeManager();
        DocCategory MACHINES = DocumentationRegistry.MAGICAL_SYSTEMS;
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

        for (PerkItem perk : PerkRegistry.getPerkItemMap().values()) {
            if(perk.perk instanceof EmptyPerk)
                continue;

            ItemStack renderStack = perk.getDefaultInstance();
            var entry = new DocEntry(perk.perk.getRegistryName(), renderStack, perk.perk.getPerkName());

            entry.addPage(TextEntry.create(Component.translatable(perk.perk.getDescriptionKey()),Component.literal(perk.perk.getName()), renderStack));

            entry.addPages(getRecipePages(renderStack, RegistryHelper.getRegistryName(perk)));


            DocumentationRegistry.registerEntry(DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT, entry);

        }

        for (AbstractFamiliarHolder r : FamiliarRegistry.getFamiliarHolderMap().values()) {
            ItemStack renderstack = r.getOutputItem();
            var entry = new DocEntry(r.getRegistryName(), renderstack, Component.translatable("entity.ars_nouveau." + r.getRegistryName().getPath()));

            entry.addPage(TextEntry.create(r.getLangDescription(), renderstack.getHoverName(), renderstack));

            DocumentationRegistry.registerEntry(DocumentationRegistry.ITEMS_BLOCKS_EQUIPMENT, entry);
        }

        addPage(new DocEntryBuilder(MACHINES, BlockRegistry.IMBUEMENT_BLOCK)
                        .withIntroPage()
                .withCraftingPages()
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_lapis"))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_amethyst"))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_amethyst_block"))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.FIRE_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.EARTH_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.WATER_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.AIR_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.ABJURATION_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.CONJURATION_ESSENCE.getRegistryName()))
                .withCraftingPages(ResourceLocation.tryParse("ars_nouveau:imbuement_" + ItemsRegistry.MANIPULATION_ESSENCE.getRegistryName())));

        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.AddEntries());
        NeoForge.EVENT_BUS.post(new ReloadDocumentationEvent.Post());
    }

    private static void addPage(DocEntryBuilder builder){
        DocumentationRegistry.registerEntry(builder.category, builder.build());
    }

    public static List<SinglePageCtor> getRecipePages(ItemStack stack, ResourceLocation recipeId){
        return getRecipePages(recipeId);
    }

    public static List<SinglePageCtor> getRecipePages(ResourceLocation recipeId){
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

        RecipeHolder<ImbuementRecipe> imbuementRecipe = manager.byKeyTyped(RecipeRegistry.IMBUEMENT_TYPE.get(), recipeId);
        if(imbuementRecipe != null){
            pages.add(ImbuementRecipeEntry.create(imbuementRecipe));
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
