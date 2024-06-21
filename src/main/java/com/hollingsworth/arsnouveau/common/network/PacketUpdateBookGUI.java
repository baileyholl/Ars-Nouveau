package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketUpdateBookGUI {

    public ItemStack bookStack;

    //Decoder
    public PacketUpdateBookGUI(FriendlyByteBuf buf) {
        bookStack = buf.readItem();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(bookStack);
    }

    public PacketUpdateBookGUI(ItemStack stack) {
        this.bookStack = stack;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ArsNouveau.proxy.getMinecraft().screen instanceof GuiSpellBook)
                ((GuiSpellBook) ArsNouveau.proxy.getMinecraft().screen).bookStack = bookStack;
        });
        ctx.get().setPacketHandled(true);
    }

}
