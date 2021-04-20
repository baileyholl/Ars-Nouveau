package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistry {
    public static final IRecipeType<GlyphPressRecipe> GLYPH_TYPE = new RecipeType();
    public static final IRecipeType<EnchantingApparatusRecipe> APPARATUS_TYPE = new RecipeType();

    public static final IRecipeType<EnchantmentRecipe> ENCHANTMENT_TYPE = new RecipeType();


    public static final IRecipeSerializer<GlyphPressRecipe> PRESS_SERIALIZER = new GlyphPressRecipe.Serializer();
    public static final IRecipeSerializer<EnchantingApparatusRecipe> APPARATUS_SERIALIZER = new EnchantingApparatusRecipe.Serializer();
    public static final IRecipeSerializer<EnchantmentRecipe> ENCHANTMENT_SERIALIZER = new EnchantmentRecipe.Serializer();
    @SubscribeEvent
    public static void register(final RegistryEvent.Register<IRecipeSerializer<?>> evt) {
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, "glyph_recipe"), GLYPH_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, "enchanting_apparatus"), APPARATUS_TYPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID), ENCHANTMENT_TYPE);
        evt.getRegistry().register(PRESS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "glyph_recipe")));
        evt.getRegistry().register(APPARATUS_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "enchanting_apparatus")));
        evt.getRegistry().register(ENCHANTMENT_SERIALIZER.setRegistryName(new ResourceLocation(ArsNouveau.MODID, EnchantmentRecipe.RECIPE_ID)));
    }

    private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }
}
