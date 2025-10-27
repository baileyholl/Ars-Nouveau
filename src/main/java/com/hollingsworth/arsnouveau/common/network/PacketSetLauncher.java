package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import com.hollingsworth.arsnouveau.common.items.data.PotionLauncherData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public class PacketSetLauncher extends AbstractPacket {
    public static final Type<PacketSetLauncher> TYPE = new Type<>(ArsNouveau.prefix("set_launcher"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetLauncher> CODEC = StreamCodec.ofMember(PacketSetLauncher::toBytes, PacketSetLauncher::new);

    public int inventorySlot;

    public PacketSetLauncher(RegistryFriendlyByteBuf buf) {
        inventorySlot = buf.readInt();
    }

    public PacketSetLauncher(int slot) {
        this.inventorySlot = slot;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(inventorySlot);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (inventorySlot >= player.inventory.getContainerSize())
            return;
        ItemStack stack = player.inventory.getItem(inventorySlot);
        ItemStack launcherStack = ItemStack.EMPTY;
        if (player.getMainHandItem().getItem() instanceof FlaskCannon) {
            launcherStack = player.getMainHandItem();
        } else if (player.getOffhandItem().getItem() instanceof FlaskCannon) {
            launcherStack = player.getOffhandItem();
        }
        if (launcherStack.isEmpty()) {
            return;
        }
        IPotionProvider provider = PotionProviderRegistry.from(stack);
        if (provider != null) {
            launcherStack.set(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData(provider.getPotionData(stack), inventorySlot));
        } else {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents == null) {
                return;
            }
            stack.set(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData(contents, inventorySlot));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
