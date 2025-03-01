package com.hollingsworth.arsnouveau.common.mixin.menus;

import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin  {
    @Inject(method = "removed", at = @At("RETURN"))
    private void removed(Player player, CallbackInfo ci) {
        player.removeData(AttachmentsRegistry.OPENED_CONTAINER_VIA_INTERACT);
    }
}
