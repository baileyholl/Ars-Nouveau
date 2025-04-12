package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientTransferHandlerPacket extends AbstractPacket{

    public static final Type<ClientTransferHandlerPacket> TYPE = new Type<>(ArsNouveau.prefix("storage_transfer_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientTransferHandlerPacket> CODEC = StreamCodec.ofMember(ClientTransferHandlerPacket::toBytes, ClientTransferHandlerPacket::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<List<ItemStack>>> STACK_LIST_LIST_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list(9));

    public List<List<ItemStack>> stacks;

    public ClientTransferHandlerPacket(List<List<ItemStack>> stacks) {
        this.stacks = stacks;
    }

    public ClientTransferHandlerPacket(RegistryFriendlyByteBuf pb) {
        this.stacks = STACK_LIST_LIST_CODEC.decode(pb);
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        STACK_LIST_LIST_CODEC.encode(pb, this.stacks);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer sender) {
        if (sender.containerMenu instanceof CraftingTerminalMenu terminalScreen){
            terminalScreen.onTransferHandler(sender, this.stacks);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}