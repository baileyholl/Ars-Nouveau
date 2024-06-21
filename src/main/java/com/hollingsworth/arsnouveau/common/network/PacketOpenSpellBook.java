package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.book.InfinityGuiSpellBook;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketOpenSpellBook {
    public boolean isMainHand;

    //Decoder
    public PacketOpenSpellBook(FriendlyByteBuf buf) {
        isMainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isMainHand);
    }

    public PacketOpenSpellBook(InteractionHand hand) {
        this.isMainHand = hand == InteractionHand.MAIN_HAND;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            InteractionHand hand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (ServerConfig.INFINITE_SPELLS.get())
                InfinityGuiSpellBook.open(hand);
            else GuiSpellBook.open(hand);
        });
        ctx.get().setPacketHandled(true);
    }

}
