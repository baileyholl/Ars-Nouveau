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

import java.util.Optional;

public class ClientSlotClick extends AbstractPacket{
    public static final Type<ClientSlotClick> TYPE = new Type<>(ArsNouveau.prefix("storage_slot_click"));

    public static StreamCodec<RegistryFriendlyByteBuf, ClientSlotClick> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, (clientSlotClick -> clientSlotClick.pullOne),
            ByteBufCodecs.optional(StoredItemStack.STREAM), (clientSlotClick -> clientSlotClick.stack),
            ByteBufCodecs.VAR_INT.map(i -> StorageTerminalMenu.SlotAction.values()[i], StorageTerminalMenu.SlotAction::ordinal), (clientSlotClick -> clientSlotClick.action),
            ClientSlotClick::new
    );

    public boolean pullOne;
    public Optional<StoredItemStack> stack;
    public StorageTerminalMenu.SlotAction action;

    public ClientSlotClick(boolean pullOne, Optional<StoredItemStack> stack, StorageTerminalMenu.SlotAction action) {
        this.pullOne = pullOne;
        this.stack = stack;
        this.action = action;
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        STREAM_CODEC.encode(pb, this);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer sender) {
        if (sender.containerMenu instanceof StorageTerminalMenu terminalScreen){
            terminalScreen.onInteract(sender, this.stack.orElse(null), this.action, this.pullOne);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
