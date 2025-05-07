package com.hollingsworth.arsnouveau.common.mixin.menus;

import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @WrapOperation(method = "handleRenameItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean rename$validIfInteract(AnvilMenu instance, Player player, Operation<Boolean> original) {
        if (IAbstractContainerMenuExtension.wasOpenedWithInteract(instance)) {
            return true;
        }

        return original.call(instance, player);
    }

    @WrapOperation(method = "handleSetBeaconPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean beacon$validIfInteract(AbstractContainerMenu instance, Player player, Operation<Boolean> original) {
        if (IAbstractContainerMenuExtension.wasOpenedWithInteract(instance)) {
            return true;
        }

        return original.call(instance, player);
    }

    @WrapOperation(method = "handleSelectTrade", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/MerchantMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean merchant$validIfInteract(MerchantMenu instance, Player player, Operation<Boolean> original) {
        if (IAbstractContainerMenuExtension.wasOpenedWithInteract(instance)) {
            return true;
        }

        return original.call(instance, player);
    }

    @WrapOperation(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean containerClick$validIfInteract(AbstractContainerMenu instance, Player player, Operation<Boolean> original) {
        if (IAbstractContainerMenuExtension.wasOpenedWithInteract(instance)) {
            return true;
        }

        return original.call(instance, player);
    }

    @WrapOperation(method = "handlePlaceRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean recipe$validIfInteract(AbstractContainerMenu instance, Player player, Operation<Boolean> original) {
        if (IAbstractContainerMenuExtension.wasOpenedWithInteract(instance)) {
            return true;
        }

        return original.call(instance, player);
    }

    @WrapOperation(method = "handleContainerButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean buttonClick$validIfInteract(AbstractContainerMenu instance, Player player, Operation<Boolean> original) {
        if (IAbstractContainerMenuExtension.wasOpenedWithInteract(instance)) {
            return true;
        }

        return original.call(instance, player);
    }
}
