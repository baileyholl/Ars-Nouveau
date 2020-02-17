package com.hollingsworth.craftedmagic.crafting;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.crafting.recipes.BookUpgradeRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static com.hollingsworth.craftedmagic.InjectionUtil.Null;

public class ModCrafting {

    @ObjectHolder(ArsNouveau.MODID)
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Recipes {
        public static final IRecipeSerializer<BookUpgradeRecipe> BOOK_UPGRADE_RECIPE = Null();

        @SubscribeEvent
        public static void register(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
            event.getRegistry().registerAll(
                    new BookUpgradeRecipe.Serializer().setRegistryName(new ResourceLocation(ArsNouveau.MODID, "book_upgrade"))
            );
        }
    }

}
