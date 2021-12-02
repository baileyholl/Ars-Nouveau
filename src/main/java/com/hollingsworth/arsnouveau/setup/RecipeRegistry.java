package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {
    public static final RecipeType<GlyphPressRecipe> GLYPH_TYPE = new RecipeType();
    public static final RecipeType<EnchantingApparatusRecipe> APPARATUS_TYPE = new RecipeType();

    public static final RecipeType<EnchantmentRecipe> ENCHANTMENT_TYPE = new RecipeType();

    public static final RecipeType<EnchantmentRecipe> CRUSH_TYPE = new RecipeType();

    public static final RecipeSerializer<GlyphPressRecipe> PRESS_SERIALIZER = new GlyphPressRecipe.Serializer();
    public static final RecipeSerializer<EnchantingApparatusRecipe> APPARATUS_SERIALIZER = new EnchantingApparatusRecipe.Serializer();
    public static final RecipeSerializer<EnchantmentRecipe> ENCHANTMENT_SERIALIZER = new EnchantmentRecipe.Serializer();
    public static final RecipeSerializer<CrushRecipe> CRUSH_SERIALIZER = new CrushRecipe.Serializer();

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<RecipeSerializer<?>> evt) {
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, "glyph_recipe"), GLYPH_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, "enchanting_apparatus"), APPARATUS_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID), ENCHANTMENT_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, CrushRecipe.RECIPE_ID), CRUSH_TYPE);
        evt.getRegistry().register(PRESS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "glyph_recipe")));
        evt.getRegistry().register(APPARATUS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "enchanting_apparatus")));
        evt.getRegistry().register(ENCHANTMENT_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID)));
        evt.getRegistry().register(CRUSH_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, CrushRecipe.RECIPE_ID)));
    }

    private static class RecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }
}
