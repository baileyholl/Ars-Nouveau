package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.*;
import com.hollingsworth.arsnouveau.api.recipe.SummonRitualRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.*;
import com.hollingsworth.arsnouveau.common.tomes.CasterTomeData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@SuppressWarnings("Convert2MethodRef")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MODID);

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

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<EnchantingApparatusRecipe>> APPARATUS_TYPE = RECIPE_TYPES.register(ENCHANTING_APPARATUS_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, EnchantingApparatusRecipe.Serializer> APPARATUS_SERIALIZER = RECIPE_SERIALIZERS.register(ENCHANTING_APPARATUS_RECIPE_ID, () -> new EnchantingApparatusRecipe.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<EnchantmentRecipe>> ENCHANTMENT_TYPE = RECIPE_TYPES.register(ENCHANTMENT_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, EnchantmentRecipe.Serializer> ENCHANTMENT_SERIALIZER = RECIPE_SERIALIZERS.register(ENCHANTMENT_RECIPE_ID, () -> new EnchantmentRecipe.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<CrushRecipe>> CRUSH_TYPE = RECIPE_TYPES.register(CRUSH_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, CrushRecipe.Serializer> CRUSH_SERIALIZER = RECIPE_SERIALIZERS.register(CRUSH_RECIPE_ID, () -> new CrushRecipe.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<ImbuementRecipe>> IMBUEMENT_TYPE = RECIPE_TYPES.register(IMBUEMENT_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, ImbuementRecipe.Serializer> IMBUEMENT_SERIALIZER = RECIPE_SERIALIZERS.register(IMBUEMENT_RECIPE_ID, () -> new ImbuementRecipe.Serializer());


    public static final DeferredHolder<RecipeType<?>, ModRecipeType<BookUpgradeRecipe>> BOOK_UPGRADE_TYPE = RECIPE_TYPES.register("book_upgrade", () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, BookUpgradeRecipe.Serializer> BOOK_UPGRADE_RECIPE = RECIPE_SERIALIZERS.register("book_upgrade", () -> new BookUpgradeRecipe.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<PotionFlaskRecipe>> POTION_FLASK_TYPE = RECIPE_TYPES.register("potion_flask", () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, PotionFlaskRecipe.Serializer> POTION_FLASK_RECIPE = RECIPE_SERIALIZERS.register("potion_flask", () -> new PotionFlaskRecipe.Serializer());


    public static final DeferredHolder<RecipeType<?>, ModRecipeType<DyeRecipe>> DYE_TYPE = RECIPE_TYPES.register(DYE_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, DyeRecipe.Serializer> DYE_RECIPE = RECIPE_SERIALIZERS.register(DYE_RECIPE_ID, () -> new DyeRecipe.Serializer());


    public static final DeferredHolder<RecipeType<?>, ModRecipeType<ReactiveEnchantmentRecipe>> REACTIVE_TYPE = RECIPE_TYPES.register(REACTIVE_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, ReactiveEnchantmentRecipe.Serializer> REACTIVE_RECIPE = RECIPE_SERIALIZERS.register(REACTIVE_RECIPE_ID, () -> new ReactiveEnchantmentRecipe.Serializer());


    public static final DeferredHolder<RecipeType<?>, ModRecipeType<SpellWriteRecipe>> SPELL_WRITE_TYPE = RECIPE_TYPES.register(SPELL_WRITE_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, SpellWriteRecipe.Serializer> SPELL_WRITE_RECIPE = RECIPE_SERIALIZERS.register(SPELL_WRITE_RECIPE_ID, () -> new SpellWriteRecipe.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<GlyphRecipe>> GLYPH_TYPE = RECIPE_TYPES.register(GLYPH_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, GlyphRecipe.Serializer> GLYPH_SERIALIZER = RECIPE_SERIALIZERS.register(GLYPH_RECIPE_ID, () -> new GlyphRecipe.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<ArmorUpgradeRecipe>> ARMOR_UPGRADE_TYPE = RECIPE_TYPES.register(ARMOR_RECIPE_ID, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, ArmorUpgradeRecipe.Serializer> ARMOR_SERIALIZER = RECIPE_SERIALIZERS.register(ARMOR_RECIPE_ID, () -> new ArmorUpgradeRecipe.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<CasterTomeData>> CASTER_TOME_TYPE = RECIPE_TYPES.register(TOME_DATAPACK, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, CasterTomeData.Serializer> CASTER_TOME_SERIALIZER = RECIPE_SERIALIZERS.register(TOME_DATAPACK, () -> new CasterTomeData.Serializer());

    public static final DeferredHolder<RecipeType<?>, ModRecipeType<SummonRitualRecipe>> SUMMON_RITUAL_TYPE = RECIPE_TYPES.register(SUMMON_RITUAL_DATAPACK, () -> new ModRecipeType<>());
    public static final DeferredHolder<RecipeSerializer<?>, SummonRitualRecipe.Serializer> SUMMON_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(SUMMON_RITUAL_DATAPACK, () -> new SummonRitualRecipe.Serializer());



    public static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return BuiltInRegistries.RECIPE_TYPE.getKey(this).toString();
        }
    }

}
