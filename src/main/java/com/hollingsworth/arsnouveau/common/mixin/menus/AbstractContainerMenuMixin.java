package com.hollingsworth.arsnouveau.common.mixin.menus;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin implements IAbstractContainerMenuExtension {
    @Unique
    public boolean ars_nouveau$openedWithInteract = false;

    @Unique
    @Override
    public boolean ars_nouveau$getOpenedWithInteract() {
        return this.ars_nouveau$openedWithInteract;
    }

    @Unique
    @Override
    public void ars_nouveau$setOpenedWithInteract(boolean newValue) {
        this.ars_nouveau$openedWithInteract = newValue;
    }
}
