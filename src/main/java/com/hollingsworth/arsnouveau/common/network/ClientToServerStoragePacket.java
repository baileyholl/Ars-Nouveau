package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class ClientToServerStoragePacket extends AbstractPacket{

    public static final Type<ClientToServerStoragePacket> TYPE = new Type<>(ArsNouveau.prefix("storage_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientToServerStoragePacket> CODEC = StreamCodec.ofMember(ClientToServerStoragePacket::toBytes, ClientToServerStoragePacket::new);

    public record Data(Optional<String> search, Optional<InteractionData> interaction, Optional<List<List<ItemStack>>> craft) {
        public static StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Data::search,
                ByteBufCodecs.optional(InteractionData.STREAM_CODEC), Data::interaction,
                ByteBufCodecs.optional(ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list(9))), Data::craft,
                Data::new
        );
    }

    public record InteractionData(boolean pullOne, Optional<StoredItemStack> stack, StorageTerminalMenu.SlotAction action) {
        public static StreamCodec<RegistryFriendlyByteBuf, InteractionData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, InteractionData::pullOne,
                ByteBufCodecs.optional(StoredItemStack.STREAM), InteractionData::stack,
                ByteBufCodecs.VAR_INT.map(i -> StorageTerminalMenu.SlotAction.values()[i], StorageTerminalMenu.SlotAction::ordinal), InteractionData::action,
                InteractionData::new
        );
    }

    public Data data;

    public ClientToServerStoragePacket(Data tag) {
        this.data = tag;
    }

    public ClientToServerStoragePacket(RegistryFriendlyByteBuf pb) {
        this.data = Data.STREAM_CODEC.decode(pb);
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        Data.STREAM_CODEC.encode(pb, this.data);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer sender) {
        if (sender.containerMenu instanceof StorageTerminalMenu terminalScreen){
            terminalScreen.receive(sender, minecraftServer.registryAccess(), this.data);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
