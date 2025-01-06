package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_REG = DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CraftingTerminalMenu>> STORAGE = MENU_REG.register("storage_lectern", () -> new MenuType<>(CraftingTerminalMenu::new, FeatureFlagSet.of()));
}
