package com.hollingsworth.arsnouveau.crafting;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.crafting.recipes.BookUpgradeRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static com.hollingsworth.arsnouveau.InjectionUtil.Null;

public class ModCrafting {

    @ObjectHolder(ArsNouveau.MODID)
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Recipes {
        public static IRecipeSerializer<BookUpgradeRecipe> BOOK_UPGRADE_RECIPE = Null();

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
            BOOK_UPGRADE_RECIPE = new BookUpgradeRecipe.Serializer();
            event.getRegistry().registerAll(
                    BOOK_UPGRADE_RECIPE.setRegistryName(new ResourceLocation(ArsNouveau.MODID, "book_upgrade"))
            );
        }
    }

}
