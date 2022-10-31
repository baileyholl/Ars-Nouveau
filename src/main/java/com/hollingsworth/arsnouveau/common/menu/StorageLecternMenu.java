package com.hollingsworth.arsnouveau.common.menu;

import mcjty.lib.api.container.CapabilityContainerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StorageLecternMenu extends AbstractContainerMenu {

    protected StorageLecternMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }


    public static MenuType<StorageLecternMenu> createContainerType() {
        MenuType<StorageLecternMenu> containerType = IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            BlockEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
            if (te == null) {
                throw new IllegalStateException("Something went wrong getting the GUI");
            } else {
                return (StorageLecternMenu)te.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY).map((h) -> {
                    return (StorageLecternMenu) Objects.requireNonNull(h.createMenu(windowId, inv, inv.player));
                }).orElseThrow(RuntimeException::new);
            }
        });
        return containerType;
    }
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }
}
