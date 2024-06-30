package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PacketUpdateBookGUI extends AbstractPacket{
    public static final Type<PacketUpdateBookGUI> TYPE = new Type<>(ArsNouveau.prefix("update_book_gui"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateBookGUI> CODEC = StreamCodec.ofMember(PacketUpdateBookGUI::toBytes, PacketUpdateBookGUI::new);
    public ItemStack bookStack;

    //Decoder
    public PacketUpdateBookGUI(RegistryFriendlyByteBuf buf) {
        bookStack =  ItemStack.STREAM_CODEC.decode(buf);
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        ItemStack.STREAM_CODEC.encode(buf, bookStack);
    }

    public PacketUpdateBookGUI(ItemStack stack) {
        this.bookStack = stack;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (ArsNouveau.proxy.getMinecraft().screen instanceof GuiSpellBook)
            ((GuiSpellBook) ArsNouveau.proxy.getMinecraft().screen).bookStack = bookStack;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
