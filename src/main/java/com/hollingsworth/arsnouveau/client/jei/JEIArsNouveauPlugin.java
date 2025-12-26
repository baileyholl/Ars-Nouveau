package com.hollingsworth.arsnouveau.client.jei;

import com.google.common.base.Suppliers;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.client.container.IAutoFillTerminal;
import com.hollingsworth.arsnouveau.common.crafting.recipes.*;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.client.jei.ScryRitualRecipeCategory.SCRY_RITUAL;

@JeiPlugin
public class JEIArsNouveauPlugin implements IModPlugin {
    public static final Supplier<RecipeType<RecipeHolder<GlyphRecipe>>> GLYPH_RECIPE_TYPE =createFromDeferredVanilla(RecipeRegistry.GLYPH_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<EnchantingApparatusRecipe>>> ENCHANTING_APP_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.APPARATUS_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<EnchantmentRecipe>>> ENCHANTING_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.ENCHANTMENT_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<ArmorUpgradeRecipe>>> ARMOR_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.ARMOR_UPGRADE_TYPE);

    public static final Supplier<RecipeType<RecipeHolder<ImbuementRecipe>>> IMBUEMENT_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.IMBUEMENT_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<CrushRecipe>>> CRUSH_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.CRUSH_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<BuddingConversionRecipe>>> BUDDING_CONVERSION_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.BUDDING_CONVERSION_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<ScryRitualRecipe>>> SCRY_RITUAL_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.SCRY_RITUAL_TYPE);
    public static final Supplier<RecipeType<RecipeHolder<AlakarkinosRecipe>>> ALAKARKINOS_RECIPE_TYPE = createFromDeferredVanilla(RecipeRegistry.ALAKARKINOS_RECIPE_TYPE);

    private static <R extends Recipe<?>> Supplier<RecipeType<RecipeHolder<R>>> createFromDeferredVanilla(Supplier<? extends net.minecraft.world.item.crafting.RecipeType<R>> deferredVanillaRecipeType) {
        return Suppliers.memoize(() -> RecipeType.createFromVanilla(deferredVanillaRecipeType.get()));
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ArsNouveau.prefix("main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new GlyphRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new CrushRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new ImbuementRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new EnchantingApparatusRecipeCategory<>(registry.getJeiHelpers().getGuiHelper()),
                new ApparatusEnchantingRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new ArmorUpgradeRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new BuddingConversionRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new ScryRitualRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new AlakarkinosRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        List<RecipeHolder<GlyphRecipe>> recipeList = new ArrayList<>();
        List<RecipeHolder<EnchantingApparatusRecipe>> apparatus = new ArrayList<>();
        List<RecipeHolder<EnchantmentRecipe>> enchantments = new ArrayList<>();
        List<RecipeHolder<CrushRecipe>> crushRecipes = new ArrayList<>();
        List<RecipeHolder<ArmorUpgradeRecipe>> armorUpgrades = new ArrayList<>();

        List<RecipeHolder<ImbuementRecipe>> imbuementRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get()).stream().toList();

        List<RecipeHolder<BuddingConversionRecipe>> buddingConversionRecipes = new ArrayList<>();
        List<RecipeHolder<ScryRitualRecipe>> scryRitualRecipes = new ArrayList<>();
        List<RecipeHolder<AlakarkinosRecipe>> alakarkinosRecipes = new ArrayList<>();

        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (RecipeHolder<?> h : manager.getRecipes().stream().toList()) {
            Recipe<?> i = h.value();
            if (i instanceof GlyphRecipe) {
                recipeList.add((RecipeHolder<GlyphRecipe>) h);
            }
            if (i instanceof EnchantmentRecipe) {
                enchantments.add((RecipeHolder<EnchantmentRecipe>) h);
            } else if (i instanceof ArmorUpgradeRecipe) {
                armorUpgrades.add((RecipeHolder<ArmorUpgradeRecipe>) h);
            } else if (i instanceof EnchantingApparatusRecipe enchantingApparatusRecipe && !enchantingApparatusRecipe.excludeJei()) {
                apparatus.add((RecipeHolder<EnchantingApparatusRecipe>) h);
            }
            if (i instanceof CrushRecipe) {
                crushRecipes.add((RecipeHolder<CrushRecipe>) h);
            }
            if (i instanceof BuddingConversionRecipe) {
                buddingConversionRecipes.add((RecipeHolder<BuddingConversionRecipe>) h);
            }
            if (i instanceof ScryRitualRecipe) {
                scryRitualRecipes.add((RecipeHolder<ScryRitualRecipe>) h);
            }
            if (i instanceof AlakarkinosRecipe) {
                alakarkinosRecipes.add((RecipeHolder<AlakarkinosRecipe>) h);
            }
        }
        registry.addRecipes(GLYPH_RECIPE_TYPE.get(), recipeList);
        registry.addRecipes(CRUSH_RECIPE_TYPE.get(), crushRecipes);
        registry.addRecipes(ENCHANTING_APP_RECIPE_TYPE.get(), apparatus);
        registry.addRecipes(ENCHANTING_RECIPE_TYPE.get(), enchantments);
        registry.addRecipes(IMBUEMENT_RECIPE_TYPE.get(), imbuementRecipes);
        registry.addRecipes(ARMOR_RECIPE_TYPE.get(), armorUpgrades);
        registry.addRecipes(BUDDING_CONVERSION_RECIPE_TYPE.get(), buddingConversionRecipes);
        registry.addRecipes(SCRY_RITUAL_RECIPE_TYPE.get(), scryRitualRecipes);
        registry.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(BlockRegistry.PORTAL_BLOCK.asItem().getDefaultInstance()));
        registry.addRecipes(ALAKARKINOS_RECIPE_TYPE.get(), alakarkinosRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.SCRIBES_BLOCK), GLYPH_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(EffectCrush.INSTANCE.glyphItem), CRUSH_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.IMBUEMENT_BLOCK), IMBUEMENT_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), ENCHANTING_APP_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), ENCHANTING_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), ARMOR_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(ItemsRegistry.AMETHYST_GOLEM_CHARM), BUDDING_CONVERSION_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(RitualRegistry.getRitualItemMap().get(SCRY_RITUAL).asItem().getDefaultInstance(), SCRY_RITUAL_RECIPE_TYPE.get());
        registry.addRecipeCatalyst(new ItemStack(ItemsRegistry.ALAKARKINOS_CHARM), ALAKARKINOS_RECIPE_TYPE.get());
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
//        registration.addRecipeClickArea(CraftingTerminalScreen.class, 100, 125, 28, 23, new RecipeType[] { RecipeTypes.CRAFTING });
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        CraftingTerminalTransferHandler.registerTransferHandlers(registration);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(DyeRecipe.class, new DyeRecipeCategory());
    }

    @Override
    public void registerIngredientAliases(@NotNull IIngredientAliasRegistration registration) {

        // for each school, add an alias for the glyph
        List<SpellSchool> schools = List.of(SpellSchools.ELEMENTAL, SpellSchools.ABJURATION, SpellSchools.CONJURATION, SpellSchools.NECROMANCY, SpellSchools.MANIPULATION, SpellSchools.ELEMENTAL_AIR, SpellSchools.ELEMENTAL_EARTH, SpellSchools.ELEMENTAL_FIRE, SpellSchools.ELEMENTAL_WATER);

        for (SpellSchool school : schools) {
            registration.addAliases(VanillaTypes.ITEM_STACK, GlyphRegistry.getGlyphItemMap().values()
                            .stream()
                            .map(Supplier::get)
                            .filter(glyph -> school.isPartOfSchool(glyph.spellPart))
                            .map(Item::getDefaultInstance)
                            .toList(),
                    school.getTextComponent().getString());
        }

        for (DeferredHolder<Item, ? extends Item> entry : ItemsRegistry.ITEMS.getEntries()) {
            if (entry.get() instanceof AliasProvider aliasProvider) {
                Collection<String> aliases = aliasProvider.getAliases().stream().map(AliasProvider.Alias::toTranslationKey).toList();
                registration.addAliases(VanillaTypes.ITEM_STACK, entry.get().getDefaultInstance(), aliases);
            }
        }
    }

    private static IJeiRuntime jeiRuntime;

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        JEIArsNouveauPlugin.jeiRuntime = jeiRuntime;
    }

    static {
        IAutoFillTerminal.updateSearch.add(new IAutoFillTerminal.ISearchHandler() {

            @Override
            public void setSearch(String text) {
                if (jeiRuntime != null) {
                    if (jeiRuntime.getIngredientFilter() != null) {
                        jeiRuntime.getIngredientFilter().setFilterText(text);
                    }
                }
            }

            @Override
            public String getSearch() {
                if (jeiRuntime != null) {
                    if (jeiRuntime.getIngredientFilter() != null) {
                        return jeiRuntime.getIngredientFilter().getFilterText();
                    }
                }
                return "";
            }

            @Override
            public String getName() {
                return "JEI";
            }
        });
    }
}