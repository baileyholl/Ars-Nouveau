package com.hollingsworth.arsnouveau.common.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<MenuType<StorageLecternMenu>> STORAGE = MENU_REG.register("storage_lectern", StorageLecternMenu::createContainerType);
}
