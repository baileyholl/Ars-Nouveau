package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.*;
import com.hollingsworth.arsnouveau.api.event.ChimeraSummonEvent;
import com.hollingsworth.arsnouveau.api.recipe.BuddingConversionRecipe;
import com.hollingsworth.arsnouveau.api.recipe.SummonRitualRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.*;
import com.hollingsworth.arsnouveau.common.tomes.CasterTomeData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@SuppressWarnings("Convert2MethodRef")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, MODID);

    public static final String ENCHANTING_APPARATUS_RECIPE_ID = "enchanting_apparatus";
    public static final String ENCHANTMENT_RECIPE_ID = "enchantment";
    public static final String CRUSH_RECIPE_ID = "crush";
    public static final String IMBUEMENT_RECIPE_ID = "imbuement";
    public static final String REACTIVE_RECIPE_ID = "reactive_enchantment";
    public static final String SPELL_WRITE_RECIPE_ID = "spell_write";
    public static final String GLYPH_RECIPE_ID = "glyph";
    public static final String DYE_RECIPE_ID = "dye";
    public static final String ARMOR_RECIPE_ID = "armor_upgrade";
    public static final String TOME_DATAPACK = "caster_tome";
    public static final String SUMMON_RITUAL_DATAPACK = "summon_ritual";
    public static final String BUDDING_CONVERSION_RECIPE_ID = "budding_conversion";

    public static final RegistryObject<RecipeType<EnchantingApparatusRecipe>> APPARATUS_TYPE = RECIPE_TYPES.register(ENCHANTING_APPARATUS_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<EnchantingApparatusRecipe>> APPARATUS_SERIALIZER = RECIPE_SERIALIZERS.register(ENCHANTING_APPARATUS_RECIPE_ID, () -> new EnchantingApparatusRecipe.Serializer());

    public static final RegistryObject<RecipeType<EnchantmentRecipe>> ENCHANTMENT_TYPE = RECIPE_TYPES.register(ENCHANTMENT_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<EnchantmentRecipe>> ENCHANTMENT_SERIALIZER = RECIPE_SERIALIZERS.register(ENCHANTMENT_RECIPE_ID, () -> new EnchantmentRecipe.Serializer());

    public static final RegistryObject<RecipeType<CrushRecipe>> CRUSH_TYPE = RECIPE_TYPES.register(CRUSH_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<CrushRecipe>> CRUSH_SERIALIZER = RECIPE_SERIALIZERS.register(CRUSH_RECIPE_ID, () -> new CrushRecipe.Serializer());

    public static final RegistryObject<RecipeType<ImbuementRecipe>> IMBUEMENT_TYPE = RECIPE_TYPES.register(IMBUEMENT_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<ImbuementRecipe>> IMBUEMENT_SERIALIZER = RECIPE_SERIALIZERS.register(IMBUEMENT_RECIPE_ID, () -> new ImbuementRecipe.Serializer());


    public static final RegistryObject<RecipeType<BookUpgradeRecipe>> BOOK_UPGRADE_TYPE = RECIPE_TYPES.register("book_upgrade", () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<BookUpgradeRecipe>> BOOK_UPGRADE_RECIPE = RECIPE_SERIALIZERS.register("book_upgrade", () -> new BookUpgradeRecipe.Serializer());

    public static final RegistryObject<RecipeType<PotionFlaskRecipe>> POTION_FLASK_TYPE = RECIPE_TYPES.register("potion_flask", () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<PotionFlaskRecipe>> POTION_FLASK_RECIPE = RECIPE_SERIALIZERS.register("potion_flask", () -> new PotionFlaskRecipe.Serializer());


    public static final RegistryObject<RecipeType<DyeRecipe>> DYE_TYPE = RECIPE_TYPES.register(DYE_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<DyeRecipe>> DYE_RECIPE = RECIPE_SERIALIZERS.register(DYE_RECIPE_ID, () -> new DyeRecipe.Serializer());


    public static final RegistryObject<RecipeType<ReactiveEnchantmentRecipe>> REACTIVE_TYPE = RECIPE_TYPES.register(REACTIVE_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<ReactiveEnchantmentRecipe>> REACTIVE_RECIPE = RECIPE_SERIALIZERS.register(REACTIVE_RECIPE_ID, () -> new ReactiveEnchantmentRecipe.Serializer());


    public static final RegistryObject<RecipeType<SpellWriteRecipe>> SPELL_WRITE_TYPE = RECIPE_TYPES.register(SPELL_WRITE_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<SpellWriteRecipe>> SPELL_WRITE_RECIPE = RECIPE_SERIALIZERS.register(SPELL_WRITE_RECIPE_ID, () -> new SpellWriteRecipe.Serializer());

    public static final RegistryObject<RecipeType<GlyphRecipe>> GLYPH_TYPE = RECIPE_TYPES.register(GLYPH_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<GlyphRecipe>> GLYPH_SERIALIZER = RECIPE_SERIALIZERS.register(GLYPH_RECIPE_ID, () -> new GlyphRecipe.Serializer());

    public static final RegistryObject<RecipeType<ArmorUpgradeRecipe>> ARMOR_UPGRADE_TYPE = RECIPE_TYPES.register(ARMOR_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<ArmorUpgradeRecipe>> ARMOR_SERIALIZER = RECIPE_SERIALIZERS.register(ARMOR_RECIPE_ID, () -> new ArmorUpgradeRecipe.Serializer());

    public static final RegistryObject<RecipeType<CasterTomeData>> CASTER_TOME_TYPE = RECIPE_TYPES.register(TOME_DATAPACK, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<CasterTomeData>> CASTER_TOME_SERIALIZER = RECIPE_SERIALIZERS.register(TOME_DATAPACK, () -> new CasterTomeData.Serializer());

    public static final RegistryObject<RecipeType<SummonRitualRecipe>> SUMMON_RITUAL_TYPE = RECIPE_TYPES.register(SUMMON_RITUAL_DATAPACK, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<SummonRitualRecipe>> SUMMON_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(SUMMON_RITUAL_DATAPACK, () -> new SummonRitualRecipe.Serializer());
    public static final RegistryObject<RecipeType<BuddingConversionRecipe>> BUDDING_CONVERSION_TYPE = RECIPE_TYPES.register(BUDDING_CONVERSION_RECIPE_ID, () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<BuddingConversionRecipe>> BUDDING_CONVERSION_SERIALIZER = RECIPE_SERIALIZERS.register(BUDDING_CONVERSION_RECIPE_ID, () -> new BuddingConversionRecipe.Serializer());


    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return ForgeRegistries.RECIPE_TYPES.getKey(this).toString();
        }
    }

}
