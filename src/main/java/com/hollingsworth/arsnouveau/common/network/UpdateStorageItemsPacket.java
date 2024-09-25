package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class UpdateStorageItemsPacket extends AbstractPacket{

    public List<StoredItemStack> stacks;

    public UpdateStorageItemsPacket(List<StoredItemStack> stacks){
        this.stacks = stacks;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        super.onClientReceived(minecraft, player);
        if (minecraft.screen instanceof AbstractStorageTerminalScreen<?> terminalScreen) {
            terminalScreen.updateItems(stacks);
        }
    }
    public static final Type<UpdateStorageItemsPacket> TYPE = new Type<>(ArsNouveau.prefix("update_storage_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateStorageItemsPacket> CODEC = StreamCodec.composite(StoredItemStack.STREAM.apply(ByteBufCodecs.list()), u -> u.stacks, UpdateStorageItemsPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
