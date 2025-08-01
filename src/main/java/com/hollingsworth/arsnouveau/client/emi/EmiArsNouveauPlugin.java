package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.container.IAutoFillTerminal;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.*;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.hollingsworth.arsnouveau.setup.registry.*;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@EmiEntrypoint
public class EmiArsNouveauPlugin implements EmiPlugin {
    public static final EmiStack ENCHANTING_APPARATUS = EmiStack.of(BlockRegistry.ENCHANTING_APP_BLOCK);
    public static final EmiRecipeCategory ENCHANTING_APPARATUS_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("enchanting_apparatus"), ENCHANTING_APPARATUS);
    public static final EmiRecipeCategory APPARATUS_ENCHANTING_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("apparatus_enchanting"), ENCHANTING_APPARATUS);
    public static final EmiRecipeCategory ARMOR_UPGRADE_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("armor_upgrade"), ENCHANTING_APPARATUS);

    public static final EmiStack IMBUEMENT_CHAMBER = EmiStack.of(BlockRegistry.IMBUEMENT_BLOCK);
    public static final EmiRecipeCategory IMBUEMENT_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("imbuement"), IMBUEMENT_CHAMBER);

    public static final EmiStack SCRIBES_TABLE = EmiStack.of(BlockRegistry.SCRIBES_BLOCK);
    public static final EmiRecipeCategory GLYPH_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("glyph_recipe"), SCRIBES_TABLE);

    public static final EmiStack AMETHYST_GOLEM_CHARM = EmiStack.of(ItemsRegistry.AMETHYST_GOLEM_CHARM);
    public static final EmiRecipeCategory BUDDING_CONVERSION_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("budding_conversion"), AMETHYST_GOLEM_CHARM);

    public static final EmiStack CRUSH_GLYPH = EmiStack.of(EffectCrush.INSTANCE.glyphItem);
    public static final EmiRecipeCategory CRUSH_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("crush"), CRUSH_GLYPH);

    public static final EmiStack SCRY_TABLET = EmiStack.of(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.SCRYING)));
    public static final EmiRecipeCategory SCRY_RITUAL_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("scry_ritual"), SCRY_TABLET);

    public static final EmiStack ALAKARKINOS_CHARM = EmiStack.of(ItemsRegistry.ALAKARKINOS_CHARM);
    public static final EmiRecipeCategory ALAKARKINOS_CATEGORY = new EmiRecipeCategory(ArsNouveau.prefix("alakarkinos"), ALAKARKINOS_CHARM);

    @Override
    public void register(EmiRegistry registry) {
        this.registerCategories(registry);
        this.registerRecipes(registry);
        this.registerStacks(registry);
        registry.addRecipeHandler(MenuRegistry.STORAGE.get(), new EmiLecternRecipeHandler<>());
    }

    public void registerCategories(EmiRegistry registry) {
        registry.addCategory(ENCHANTING_APPARATUS_CATEGORY);
        registry.addWorkstation(ENCHANTING_APPARATUS_CATEGORY, ENCHANTING_APPARATUS);

        registry.addCategory(APPARATUS_ENCHANTING_CATEGORY);
        registry.addWorkstation(APPARATUS_ENCHANTING_CATEGORY, ENCHANTING_APPARATUS);

        registry.addCategory(ARMOR_UPGRADE_CATEGORY);
        registry.addWorkstation(ARMOR_UPGRADE_CATEGORY, ENCHANTING_APPARATUS);

        registry.addCategory(IMBUEMENT_CATEGORY);
        registry.addWorkstation(IMBUEMENT_CATEGORY, IMBUEMENT_CHAMBER);

        registry.addCategory(GLYPH_CATEGORY);
        registry.addWorkstation(GLYPH_CATEGORY, SCRIBES_TABLE);

        registry.addCategory(BUDDING_CONVERSION_CATEGORY);
        registry.addWorkstation(BUDDING_CONVERSION_CATEGORY, AMETHYST_GOLEM_CHARM);

        registry.addCategory(CRUSH_CATEGORY);
        registry.addWorkstation(CRUSH_CATEGORY, CRUSH_GLYPH);

        registry.addCategory(SCRY_RITUAL_CATEGORY);
        registry.addWorkstation(SCRY_RITUAL_CATEGORY, SCRY_TABLET);

        registry.addCategory(ALAKARKINOS_CATEGORY);
        registry.addWorkstation(ALAKARKINOS_CATEGORY, ALAKARKINOS_CHARM);
    }

    public void registerRecipes(@NotNull EmiRegistry registry) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (RecipeHolder<?> i : manager.getRecipes()) {
            var id = i.id();
            var emiRecipe = switch (i.value()) {
                case GlyphRecipe glyphRecipe -> new EmiGlyphRecipe(id, glyphRecipe);
                case EnchantmentRecipe enchantmentRecipe -> new EmiApparatusEnchantingRecipe(id, enchantmentRecipe);
                case ArmorUpgradeRecipe upgradeRecipe -> new EmiArmorUpgradeRecipe(id, upgradeRecipe);
                case EnchantingApparatusRecipe enchantingApparatusRecipe when !enchantingApparatusRecipe.excludeJei() ->
                        new EmiEnchantingApparatusRecipe<>(id, enchantingApparatusRecipe);
                case CrushRecipe crushRecipe -> new EmiCrushRecipe(id, crushRecipe);
                case BuddingConversionRecipe buddingConversionRecipe ->
                        new EmiBuddingConversionRecipe(id, buddingConversionRecipe);
                case ScryRitualRecipe scryRitualRecipe -> new EmiScryRitualRecipe(id, scryRitualRecipe);
                case AlakarkinosRecipe alakarkinosRecipe -> new EmiAlakarkinosRecipe(id, alakarkinosRecipe);
                default -> null;
            };

            if (emiRecipe != null) {
                registry.addRecipe(emiRecipe);
            }
        }

        for (var recipe : Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get())) {
            registry.addRecipe(new EmiImbuementRecipe(recipe.id(), recipe.value()));
        }

        registry.addRecipe(EmiWorldInteractionRecipe
                .builder()
                .id(ArsNouveau.prefix("/interaction/drygmy_henge"))
                .leftInput(EmiStack.of(Items.MOSSY_COBBLESTONE))
                .rightInput(EmiStack.of(ItemsRegistry.DRYGMY_CHARM), false)
                .output(EmiStack.of(BlockRegistry.DRYGMY_BLOCK))
                .build());

        registry.addRecipe(EmiWorldInteractionRecipe
                .builder()
                .id(ArsNouveau.prefix("/interaction/whirlisprig_flower"))
                .leftInput(EmiIngredient.of(BlockTags.FLOWERS))
                .rightInput(EmiStack.of(ItemsRegistry.WHIRLISPRIG_CHARM), false)
                .output(EmiStack.of(BlockRegistry.WHIRLISPRIG_FLOWER))
                .build());

        registry.addRecipe(EmiWorldInteractionRecipe
                .builder()
                .id(ArsNouveau.prefix("/interaction/wixie_cauldron"))
                .leftInput(EmiStack.of(Items.CAULDRON))
                .rightInput(EmiStack.of(ItemsRegistry.WIXIE_CHARM), false)
                .output(EmiStack.of(BlockRegistry.WIXIE_CAULDRON))
                .build());

        registry.addRecipe(EmiWorldInteractionRecipe
                .builder()
                .id(ArsNouveau.prefix("/interaction/scryer_scroll"))
                .leftInput(EmiStack.of(ItemsRegistry.BLANK_PARCHMENT))
                .rightInput(EmiStack.of(BlockRegistry.SCRYERS_CRYSTAL), true)
                .output(EmiStack.of(ItemsRegistry.SCRYER_SCROLL))
                .build());

        var dirtStacks = EmiIngredient.of(BlockTags.DIRT).getEmiStacks();
        List<EmiStack> nonGrassDirtStacks = new ArrayList<>();
        for (var stack : dirtStacks) {
            if (!stack.getItemStack().is(Items.GRASS_BLOCK)) {
                nonGrassDirtStacks.add(stack);
            }
        }
        var nonGrassDirt = EmiIngredient.of(nonGrassDirtStacks);

        registry.addRecipe(EmiWorldInteractionRecipe
                .builder()
                .id(ArsNouveau.prefix("/interaction/earth_essence_grass"))
                .leftInput(nonGrassDirt)
                .rightInput(EmiStack.of(ItemsRegistry.EARTH_ESSENCE), false)
                .output(EmiStack.of(Blocks.GRASS_BLOCK))
                .build());
    }

    public void registerStacks(@NotNull EmiRegistry registry) {
        var fullSourceJar = BlockRegistry.SOURCE_JAR.asItem().getDefaultInstance();
        var fullSourceJarTag = new CompoundTag();
        fullSourceJarTag.putString("id", BlockRegistry.SOURCE_JAR_TILE.registryObject.getId().toString());
        fullSourceJarTag.putInt(SourceJarTile.SOURCE_TAG, 10000);
        fullSourceJarTag.putInt(SourceJarTile.COLOR_TAG, ParticleColor.defaultParticleColor().getColor());
        fullSourceJar.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(fullSourceJarTag));
        fullSourceJar.set(DataComponentRegistry.BLOCK_FILL_CONTENTS, new BlockFillContents(10000));

        registry.addEmiStackAfter(EmiStack.of(fullSourceJar), EmiStack.of(BlockRegistry.SOURCE_JAR.asItem()));

        for (var item : new AnimatedMagicArmor[]{
                ItemsRegistry.SORCERER_HOOD.get(), ItemsRegistry.SORCERER_ROBES.get(), ItemsRegistry.SORCERER_LEGGINGS.get(), ItemsRegistry.SORCERER_BOOTS.get(),
                ItemsRegistry.ARCANIST_HOOD.get(), ItemsRegistry.ARCANIST_ROBES.get(), ItemsRegistry.ARCANIST_LEGGINGS.get(), ItemsRegistry.ARCANIST_BOOTS.get(),
                ItemsRegistry.BATTLEMAGE_HOOD.get(), ItemsRegistry.BATTLEMAGE_ROBES.get(), ItemsRegistry.BATTLEMAGE_LEGGINGS.get(), ItemsRegistry.BATTLEMAGE_BOOTS.get(),
        }) {
            var stack = item.getDefaultInstance();
            IPerkHolder<ArmorPerkHolder> perkHolder = PerkUtil.getPerkHolder(stack);
            if (perkHolder == null) {
                continue;
            }

            for (int tier = 1; tier <= 2; tier++) {
                stack.set(DataComponentRegistry.ARMOR_PERKS, perkHolder.setTier(tier));
                final int finalTier = tier;
                registry.addEmiStackAfter(EmiStack.of(stack.copy()), s -> s.getItemStack().is(stack.getItem()) && s.getItemStack().get(DataComponentRegistry.ARMOR_PERKS).getTier() == finalTier - 1);
            }
        }

        registry.removeEmiStacks(EmiStack.of(BlockRegistry.RUNE_BLOCK));
        registry.removeEmiStacks(EmiStack.of(BlockRegistry.MAGIC_FIRE));
        registry.removeEmiStacks(EmiStack.of(BlockRegistry.MAGE_BLOCK));
        registry.removeEmiStacks(EmiStack.of(BlockRegistry.PORTAL_BLOCK));
        registry.removeEmiStacks(EmiStack.of(BlockRegistry.CRAB_HAT));
        registry.removeEmiStacks(EmiStack.of(ItemsRegistry.BLANK_GLYPH));
        registry.removeEmiStacks(EmiStack.of(ItemsRegistry.debug));
    }

    static {
        IAutoFillTerminal.updateSearch.add(new IAutoFillTerminal.ISearchHandler() {
            @Override
            public void setSearch(String text) {
                EmiApi.setSearchText(text);
            }

            @Override
            public String getSearch() {
                return EmiApi.getSearchText();
            }

            @Override
            public String getName() {
                return "EMI";
            }
        });
    }
}