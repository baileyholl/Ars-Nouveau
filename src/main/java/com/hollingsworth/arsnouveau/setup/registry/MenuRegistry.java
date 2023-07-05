package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<MenuType<CraftingTerminalMenu>> STORAGE = MENU_REG.register("storage_lectern", () -> new MenuType<>(CraftingTerminalMenu::new, FeatureFlagSet.of()));
}
