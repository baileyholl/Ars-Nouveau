package com.hollingsworth.arsnouveau.common.mixin.menus;

import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean validIfInteract(AbstractContainerMenu instance, Player player, Operation<Boolean> original) {
        if (player.getData(AttachmentsRegistry.OPENED_CONTAINER_VIA_INTERACT) == player.containerMenu.containerId) {
            return true;
        }

        return original.call(instance, player);
    }
}
