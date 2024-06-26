package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.book.InfinityGuiSpellBook;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;


public class PacketOpenSpellBook extends AbstractPacket{
    boolean isMainHand;

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenSpellBook> CODEC  = StreamCodec.ofMember(PacketOpenSpellBook::toBytes, PacketOpenSpellBook::new);
    public static final Type<PacketOpenSpellBook> TYPE = new Type<>(ArsNouveau.prefix("open_spell_book"));

    public PacketOpenSpellBook(boolean isMainHand) {
        this.isMainHand = isMainHand;
    }

    //Decoder
    public PacketOpenSpellBook(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(isMainHand);
    }
    
    public PacketOpenSpellBook(InteractionHand hand) {
        this.isMainHand = hand == InteractionHand.MAIN_HAND;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        InteractionHand hand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if (ServerConfig.INFINITE_SPELLS.get())
            InfinityGuiSpellBook.open(hand);
        else GuiSpellBook.open(hand);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
