package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class GlyphRecipeProvider extends SimpleDataProvider {

    public List<GlyphRecipe> recipes = new ArrayList<>();

    public GlyphRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {

        add(get(AugmentAccelerate.INSTANCE).withItem(Items.POWERED_RAIL).withItem(Items.SUGAR).withItem(Items.CLOCK));
        add(get(AugmentDecelerate.INSTANCE).withItem(Items.SOUL_SAND).withItem(Items.COBWEB).withItem(Items.CLOCK));
        add(get(AugmentAmplify.INSTANCE).withItem(Items.DIAMOND_PICKAXE));
        add(get(AugmentAOE.INSTANCE).withItem(Items.FIREWORK_STAR));
        add(get(AugmentDampen.INSTANCE).withItem(Items.NETHER_BRICK));
        add(get(AugmentDurationDown.INSTANCE).withItem(Items.CLOCK).withItem(Items.GLOWSTONE_DUST));
        add(get(AugmentExtendTime.INSTANCE).withItem(Items.CLOCK).withIngredient(Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE)));
        add(get(AugmentExtract.INSTANCE).withItem(Items.EMERALD));
        add(get(AugmentFortune.INSTANCE).withItem(Items.RABBIT_FOOT));
        add(get(AugmentPierce.INSTANCE).withItem(Items.ARROW).withItem(ItemsRegistry.WILDEN_SPIKE));
        add(get(AugmentSensitive.INSTANCE).withItem(Items.SCAFFOLDING).withItem(Items.POPPY).withItem(Items.WATER_BUCKET));
        add(get(AugmentSplit.INSTANCE).withItem(BlockRegistry.RELAY_SPLITTER).withItem(ItemsRegistry.WILDEN_SPIKE).withItem(Items.STONECUTTER));

        add(get(EffectOrbit.INSTANCE).withItem(Items.COMPASS).withItem(Items.ENDER_EYE).withIngredient(Ingredient.of(Tags.Items.RODS_BLAZE)));
        add(get(MethodProjectile.INSTANCE).withItem(Items.FLETCHING_TABLE).withItem(Items.ARROW));
        add(get(MethodSelf.INSTANCE).withIngredient(Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES)).withItem(Items.IRON_CHESTPLATE));
        add(get(MethodTouch.INSTANCE).withIngredient(Ingredient.of(ItemTags.BUTTONS)));
        add(get(MethodUnderfoot.INSTANCE).withItem(Items.IRON_BOOTS).withIngredient(Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES)));

        add(get(EffectBlink.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withIngredient(Ingredient.of(Tags.Items.ENDER_PEARLS), 4));
        add(get(EffectBounce.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withIngredient(Ingredient.of(Tags.Items.SLIME_BALLS), 3));
        add(get(EffectBreak.INSTANCE).withItem(Items.IRON_PICKAXE));
        add(get(EffectColdSnap.INSTANCE).withItem(ItemsRegistry.WATER_ESSENCE).withItem(Items.POWDER_SNOW_BUCKET).withItem(Items.ICE));
        add(get(EffectConjureWater.INSTANCE).withItem(ItemsRegistry.WATER_ESSENCE).withItem(Items.WATER_BUCKET));
        add(get(EffectCraft.INSTANCE).withItem(Items.CRAFTING_TABLE));
        add(get(EffectCrush.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.GRINDSTONE).withItem(Items.PISTON));
        add(get(EffectCut.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.SHEARS).withItem(Items.IRON_SWORD));
        add(get(EffectDelay.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.REPEATER).withItem(Items.CLOCK));
        add(get(EffectDispel.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(Items.MILK_BUCKET, 3));

        add(get(EffectEnderChest.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.ENDER_CHEST));
        add(get(EffectEvaporate.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.SPONGE, 3));

        add(get(EffectExchange.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.EMERALD_BLOCK).withIngredient(Ingredient.of(Tags.Items.ENDER_PEARLS), 2));
        add(get(EffectExplosion.INSTANCE).withItem(ItemsRegistry.FIRE_ESSENCE).withItem(Items.TNT, 3).withItem(Items.FIRE_CHARGE));
        add(get(EffectFangs.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withItem(Items.PRISMARINE_SHARD, 2).withItem(Items.TOTEM_OF_UNDYING));
        add(get(EffectFell.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.DIAMOND_AXE));
        add(get(EffectFirework.INSTANCE).withItem(ItemsRegistry.FIRE_ESSENCE).withItem(Items.FIREWORK_ROCKET, 2).withItem(Items.FIREWORK_STAR));
        add(get(EffectFlare.INSTANCE).withItem(ItemsRegistry.FIRE_ESSENCE).withItem(Items.FLINT_AND_STEEL, 2).withItem(Items.FIRE_CHARGE, 2).withItem(Items.BLAZE_ROD));
        add(get(EffectFreeze.INSTANCE).withItem(ItemsRegistry.WATER_ESSENCE).withItem(Items.SNOW_BLOCK, 2));
        add(get(EffectGlide.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(Items.ELYTRA).withIngredient(Ingredient.of(Tags.Items.GEMS_DIAMOND), 3));
        add(get(EffectGravity.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(Items.ANVIL, 2).withIngredient(Ingredient.of(Tags.Items.FEATHERS), 3));
        add(get(EffectGrow.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.BONE_BLOCK, 5).withIngredient(Ingredient.of(Tags.Items.SEEDS), 3));
        add(get(EffectHarm.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.IRON_SWORD, 3));
        add(get(EffectHarvest.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.IRON_HOE, 1));
        add(get(EffectHeal.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(Items.GLISTERING_MELON_SLICE, 4).withItem(Items.GOLDEN_APPLE));
        add(get(EffectHex.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(Items.FERMENTED_SPIDER_EYE).withItem(Items.BLAZE_ROD, 3).withItem(Items.WITHER_ROSE));
        add(get(EffectIgnite.INSTANCE).withItem(Items.FLINT_AND_STEEL).withIngredient(ItemTags.COALS, 3));
        add(get(EffectIntangible.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.PHANTOM_MEMBRANE, 3).withIngredient(Ingredient.of(Tags.Items.ENDER_PEARLS), 2));
        add(get(EffectInteract.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.LEVER).withIngredient(Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES)).withIngredient(Ingredient.of(ItemTags.BUTTONS)));
        add(get(EffectInvisibility.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(Items.FERMENTED_SPIDER_EYE).withIngredient(Ingredient.of(Tags.Items.RODS_BLAZE)));
        add(get(EffectKnockback.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(Items.PISTON, 3));
        add(get(EffectLaunch.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(Items.RABBIT_HIDE, 3));
        add(get(EffectLeap.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(ItemsRegistry.WILDEN_WING, 3));
        add(get(EffectLight.INSTANCE).withItem(Items.LANTERN).withItem(Items.TORCH));
        add(get(EffectLightning.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(Items.LIGHTNING_ROD, 3).withItem(Items.HEART_OF_THE_SEA));
        add(get(EffectLinger.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.DRAGON_BREATH)
                .withIngredient(Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .withIngredient(Ingredient.of(Tags.Items.RODS_BLAZE), 2));
        add(get(EffectPhantomBlock.INSTANCE).withIngredient(Tags.Items.GLASS_BLOCKS, 8));
        add(get(EffectPickup.INSTANCE).withItem(Items.HOPPER, 2));
        add(get(EffectPull.INSTANCE).withItem(Items.FISHING_ROD, 1));
        add(get(EffectRedstone.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withIngredient(Tags.Items.STORAGE_BLOCKS_REDSTONE, 3));
        add(get(EffectRune.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(ItemsRegistry.RUNIC_CHALK).withItem(Items.TRIPWIRE_HOOK));
        add(get(EffectSlowfall.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(ItemsRegistry.WILDEN_WING).withItem(Items.FEATHER, 3)
                .withIngredient(Tags.Items.RODS_BLAZE, 1).withIngredient(Tags.Items.CROPS_NETHER_WART, 1));
        add(get(EffectSmelt.INSTANCE).withItem(ItemsRegistry.FIRE_ESSENCE).withItem(Items.BLAST_FURNACE, 4).withIngredient(Tags.Items.RODS_BLAZE, 1));
        add(get(EffectSnare.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.COBWEB, 4));
        add(get(EffectSummonDecoy.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withItem(Items.ARMOR_STAND, 4));
        add(get(EffectSummonSteed.INSTANCE).withItem(Items.LEATHER, 4));
        add(get(EffectSummonVex.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withItem(Items.TOTEM_OF_UNDYING, 1));
        add(get(EffectSummonWolves.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withItem(Items.BONE, 3).withItem(ItemsRegistry.WILDEN_WING, 4));
        add(get(EffectToss.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.DROPPER, 1));
        add(get(EffectWindshear.INSTANCE).withItem(ItemsRegistry.AIR_ESSENCE).withItem(Items.IRON_SWORD, 3));
        add(get(EffectWither.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(Items.WITHER_SKELETON_SKULL, 3));
        add(get(EffectPlaceBlock.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.DISPENSER));
        add(get(EffectSummonUndead.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withItem(Items.BONE, 1).withItem(Items.WITHER_SKELETON_SKULL));
        add(get(EffectName.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.NAME_TAG));
        add(get(EffectSenseMagic.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(ItemsRegistry.DOWSING_ROD).withItem(ItemsRegistry.STARBUNCLE_SHARD));
        add(get(EffectInfuse.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(Items.GLASS_BOTTLE).withIngredient(Tags.Items.RODS_BLAZE, 1));
        add(get(EffectWall.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.DRAGON_BREATH)
                .withIngredient(Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND))
                .withIngredient(Ingredient.of(Tags.Items.RODS_BLAZE), 2));
        add(get(EffectRotate.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE));
        add(get(EffectAnimate.INSTANCE).withItem(ItemsRegistry.CONJURATION_ESSENCE).withIngredient(Tags.Items.OBSIDIANS, 3));
        add(get(EffectBurst.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.TNT, 5).withItem(Items.FIREWORK_STAR));
        add(get(AugmentRandomize.INSTANCE).withItem(Items.PINK_CARPET, 2));
        add(get(EffectReset.INSTANCE).withItem(Items.TARGET, 1));
        add(get(EffectWololo.INSTANCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withIngredient(Ingredient.of(Tags.Items.DYES), 3));
        add(get(EffectRewind.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.CLOCK, 3));
        add(get(MethodPantomime.INSTANCE).withIngredient(Tags.Items.GLASS_BLOCKS, 8));
        add(get(EffectBubble.INSTANCE).withIngredient(Tags.Items.FEATHERS, 3).withItem(Items.WATER_BUCKET).withIngredient(ItemTags.BOATS, 1).withItem(ItemsRegistry.WATER_ESSENCE));
        for (GlyphRecipe recipe : recipes) {
            Path path = getScribeGlyphPath(output, recipe.output.getItem());
            saveStable(pOutput, GlyphRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow(), path);
        }
    }

    public void add(GlyphRecipe recipe) {
        recipes.add(recipe);
    }

    public GlyphRecipe get(AbstractSpellPart spellPart) {
        return new GlyphRecipe(spellPart.glyphItem.getDefaultInstance(), new ArrayList<>(), getExpFromTier(spellPart));
    }

    public int getExpFromTier(AbstractSpellPart spellPart) {

        return switch (spellPart.defaultTier().value) {
            case (1):
                yield 27;
            case (2):
                yield 55;
            case 3:
                yield 160;
            default:
                yield 0;
        };
    }

    protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        return pathIn.resolve("data/ars_nouveau/recipe/" + getRegistryName(glyph).getPath() + ".json");

    }

    @Override
    public @NotNull String getName() {
        return "Glyph Recipes";
    }
}
