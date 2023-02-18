package com.hollingsworth.arsnouveau.common.menu;

import com.hollingsworth.arsnouveau.common.block.tile.StorageControllerBlockEntity;
import com.hollingsworth.arsnouveau.common.block.tile.container.StorageControllerContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final RegistryObject<MenuType<StorageControllerContainer>> LECTERN =
            MENU_REG.register("storage_lectern",
                    () -> IForgeMenuType
                            .create((windowId, inv, data) -> new StorageControllerContainer(windowId, inv,
                                    (StorageControllerBlockEntity) inv.player.level
                                            .getBlockEntity(data.readBlockPos()))));

}
