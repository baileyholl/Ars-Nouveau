package com.hollingsworth.arsnouveau.common.mixin.menus;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IAbstractContainerMenuExtension {
    boolean ars_nouveau$getOpenedWithInteract();
    void ars_nouveau$setOpenedWithInteract(boolean newValue);

    static boolean wasOpenedWithInteract(AbstractContainerMenu menu) {
        return menu instanceof IAbstractContainerMenuExtension ext && ext.ars_nouveau$getOpenedWithInteract();
    }

    static void setOpenedWithInteract(AbstractContainerMenu menu, boolean newValue) {
        if (menu instanceof IAbstractContainerMenuExtension ext) {
            ext.ars_nouveau$setOpenedWithInteract(newValue);
        }
    }
}
