package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {
    public static final RecipeType<GlyphPressRecipe> GLYPH_TYPE = new ModRecipeType();
    public static final RecipeType<EnchantingApparatusRecipe> APPARATUS_TYPE = new ModRecipeType();

    public static final RecipeType<EnchantmentRecipe> ENCHANTMENT_TYPE = new ModRecipeType();

    public static final RecipeType<CrushRecipe> CRUSH_TYPE = new ModRecipeType();
    public static final RecipeType<InfuserRecipe> INFUSER_TYPE = new ModRecipeType<>();

    public static final RecipeType<BookUpgradeRecipe> BOOK_UPGRADE_TYPE = new ModRecipeType();
    public static final RecipeType<PotionFlaskRecipe> POTION_FLASK_TYPE = new ModRecipeType();
    public static final RecipeType<DyeRecipe> DYE_TYPE = new ModRecipeType<>();
    public static final RecipeType<ReactiveEnchantmentRecipe> REACTIVE_TYPE = new ModRecipeType<>();

    public static final RecipeSerializer<GlyphPressRecipe> PRESS_SERIALIZER = new GlyphPressRecipe.Serializer();
    public static final RecipeSerializer<EnchantingApparatusRecipe> APPARATUS_SERIALIZER = new EnchantingApparatusRecipe.Serializer();
    public static final RecipeSerializer<EnchantmentRecipe> ENCHANTMENT_SERIALIZER = new EnchantmentRecipe.Serializer();
    public static final RecipeSerializer<CrushRecipe> CRUSH_SERIALIZER = new CrushRecipe.Serializer();
    public static final RecipeSerializer<InfuserRecipe> INFUSER_SERIALIZER = new InfuserRecipe.Serializer();
    public static final RecipeSerializer<BookUpgradeRecipe> BOOK_UPGRADE_RECIPE =  new BookUpgradeRecipe.Serializer();
    public static final RecipeSerializer<PotionFlaskRecipe> POTION_FLASK_RECIPE = new PotionFlaskRecipe.Serializer();
    public static final RecipeSerializer<DyeRecipe> DYE_RECIPE = new DyeRecipe.Serializer();
    public static final RecipeSerializer<ReactiveEnchantmentRecipe> REACTIVE_RECIPE = new ReactiveEnchantmentRecipe.Serializer();

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<RecipeSerializer<?>> evt) {

        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, GlyphPressRecipe.RECIPE_ID), GLYPH_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, EnchantingApparatusRecipe.RECIPE_ID), APPARATUS_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID), ENCHANTMENT_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, CrushRecipe.RECIPE_ID), CRUSH_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, InfuserRecipe.RECIPE_ID), INFUSER_TYPE);

        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, "book_upgrade"), BOOK_UPGRADE_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, "potion_flask"), POTION_FLASK_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, "dye"), DYE_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, ReactiveEnchantmentRecipe.RECIPE_ID), REACTIVE_TYPE);

        evt.getRegistry().register(PRESS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, GlyphPressRecipe.RECIPE_ID)));
        evt.getRegistry().register(APPARATUS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, EnchantingApparatusRecipe.RECIPE_ID)));
        evt.getRegistry().register(ENCHANTMENT_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID)));
        evt.getRegistry().register(CRUSH_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, CrushRecipe.RECIPE_ID)));
        evt.getRegistry().register(INFUSER_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, InfuserRecipe.RECIPE_ID)));
        evt.getRegistry().register(REACTIVE_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, ReactiveEnchantmentRecipe.RECIPE_ID)));

        evt.getRegistry().registerAll(
                BOOK_UPGRADE_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "book_upgrade")),
                POTION_FLASK_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "potion_flask")),
                DYE_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "dye"))
        );
    }

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }
}
