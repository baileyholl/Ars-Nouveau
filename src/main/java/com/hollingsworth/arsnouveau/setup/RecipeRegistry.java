package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.*;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);


    public static final RegistryObject<RecipeType<EnchantingApparatusRecipe>> APPARATUS_TYPE = RECIPE_TYPES.register(EnchantingApparatusRecipe.RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<EnchantingApparatusRecipe>> APPARATUS_SERIALIZER = RECIPE_SERIALIZERS.register(EnchantingApparatusRecipe.RECIPE_ID, EnchantingApparatusRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<EnchantmentRecipe>> ENCHANTMENT_TYPE = RECIPE_TYPES.register(EnchantmentRecipe.RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<EnchantmentRecipe>> ENCHANTMENT_SERIALIZER = RECIPE_SERIALIZERS.register(EnchantmentRecipe.RECIPE_ID, EnchantmentRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<CrushRecipe>> CRUSH_TYPE = RECIPE_TYPES.register(CrushRecipe.RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<CrushRecipe>> CRUSH_SERIALIZER = RECIPE_SERIALIZERS.register(CrushRecipe.RECIPE_ID, CrushRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<ImbuementRecipe>> IMBUEMENT_TYPE = RECIPE_TYPES.register(ImbuementRecipe.RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<ImbuementRecipe>> IMBUEMENT_SERIALIZER = RECIPE_SERIALIZERS.register(ImbuementRecipe.RECIPE_ID, ImbuementRecipe.Serializer::new);


    public static final RegistryObject<RecipeType<BookUpgradeRecipe>> BOOK_UPGRADE_TYPE = RECIPE_TYPES.register("book_upgrade", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<BookUpgradeRecipe>> BOOK_UPGRADE_RECIPE = RECIPE_SERIALIZERS.register("book_upgrade", BookUpgradeRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<PotionFlaskRecipe>> POTION_FLASK_TYPE = RECIPE_TYPES.register("potion_flask", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<PotionFlaskRecipe>> POTION_FLASK_RECIPE = RECIPE_SERIALIZERS.register("potion_flask", PotionFlaskRecipe.Serializer::new);


    public static final RegistryObject<RecipeType<DyeRecipe>> DYE_TYPE = RECIPE_TYPES.register("dye", ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<DyeRecipe>> DYE_RECIPE = RECIPE_SERIALIZERS.register("dye", DyeRecipe.Serializer::new);


    public static final RegistryObject<RecipeType<ReactiveEnchantmentRecipe>> REACTIVE_TYPE = RECIPE_TYPES.register(ReactiveEnchantmentRecipe.RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<ReactiveEnchantmentRecipe>> REACTIVE_RECIPE = RECIPE_SERIALIZERS.register(ReactiveEnchantmentRecipe.RECIPE_ID, ReactiveEnchantmentRecipe.Serializer::new);


    public static final RegistryObject<RecipeType<SpellWriteRecipe>> SPELL_WRITE_TYPE = RECIPE_TYPES.register(SpellWriteRecipe.RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<SpellWriteRecipe>> SPELL_WRITE_RECIPE = RECIPE_SERIALIZERS.register(SpellWriteRecipe.RECIPE_ID, SpellWriteRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<GlyphRecipe>> GLYPH_TYPE = RECIPE_TYPES.register(GlyphRecipe.RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<GlyphRecipe>> GLYPH_SERIALIZER = RECIPE_SERIALIZERS.register(GlyphRecipe.RECIPE_ID, GlyphRecipe.Serializer::new);

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }

}
