package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.InfuserRecipe;
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

    public static final RecipeSerializer<GlyphPressRecipe> PRESS_SERIALIZER = new GlyphPressRecipe.Serializer();
    public static final RecipeSerializer<EnchantingApparatusRecipe> APPARATUS_SERIALIZER = new EnchantingApparatusRecipe.Serializer();
    public static final RecipeSerializer<EnchantmentRecipe> ENCHANTMENT_SERIALIZER = new EnchantmentRecipe.Serializer();
    public static final RecipeSerializer<CrushRecipe> CRUSH_SERIALIZER = new CrushRecipe.Serializer();
    public static final RecipeSerializer<InfuserRecipe> INFUSER_SERIALIZER = new InfuserRecipe.Serializer();

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<RecipeSerializer<?>> evt) {
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, GlyphPressRecipe.RECIPE_ID), GLYPH_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, EnchantingApparatusRecipe.RECIPE_ID), APPARATUS_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID), ENCHANTMENT_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, CrushRecipe.RECIPE_ID), CRUSH_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, InfuserRecipe.RECIPE_ID), INFUSER_TYPE);

        evt.getRegistry().register(PRESS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, GlyphPressRecipe.RECIPE_ID)));
        evt.getRegistry().register(APPARATUS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, EnchantingApparatusRecipe.RECIPE_ID)));
        evt.getRegistry().register(ENCHANTMENT_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID)));
        evt.getRegistry().register(CRUSH_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, CrushRecipe.RECIPE_ID)));
        evt.getRegistry().register(INFUSER_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, InfuserRecipe.RECIPE_ID)));
    }

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }
}
