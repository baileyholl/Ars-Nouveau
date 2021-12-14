package com.hollingsworth.arsnouveau.common.crafting;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.BookUpgradeRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.DyeRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.PotionFlaskRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static com.hollingsworth.arsnouveau.setup.InjectionUtil.Null;

public class ModCrafting {

    @ObjectHolder(ArsNouveau.MODID)
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Recipes {
        public static RecipeSerializer<BookUpgradeRecipe> BOOK_UPGRADE_RECIPE = Null();
        public static RecipeSerializer<PotionFlaskRecipe> POTION_FLASK_RECIPE = Null();
        public static RecipeSerializer<DyeRecipe> DYE_RECIPE = Null();

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            BOOK_UPGRADE_RECIPE = new BookUpgradeRecipe.Serializer();
            POTION_FLASK_RECIPE = new PotionFlaskRecipe.Serializer();
            DYE_RECIPE = new DyeRecipe.Serializer();
            event.getRegistry().registerAll(
                    BOOK_UPGRADE_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "book_upgrade")),
                    POTION_FLASK_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "potion_flask")),
                    DYE_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "dye"))
            );
        }
    }

}
