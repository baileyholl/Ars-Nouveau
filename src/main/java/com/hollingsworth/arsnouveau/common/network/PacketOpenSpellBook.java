package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenSpellBook {
    public ItemStack stack;
    public int tier;


    //Decoder
    public PacketOpenSpellBook(FriendlyByteBuf buf) {
        stack = buf.readItem();
        tier = buf.readInt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(stack);
        buf.writeInt(tier);
    }

    public PacketOpenSpellBook(ItemStack stack, int tier) {
        this.stack = stack;
        this.tier = tier;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> GuiSpellBook.open(stack, tier));
        ctx.get().setPacketHandled(true);
    }

}
