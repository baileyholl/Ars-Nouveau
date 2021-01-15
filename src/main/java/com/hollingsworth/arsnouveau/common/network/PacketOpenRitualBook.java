package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.gui.book.GuiRitualBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenRitualBook {

    public PacketOpenRitualBook(){}

    //Decoder
    public PacketOpenRitualBook(PacketBuffer buf){

    }

    //Encoder
    public void toBytes(PacketBuffer buf){

    }

    public PacketOpenRitualBook(CompoundNBT tag, int tier, String unlockedSpells){

    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> GuiRitualBook.open());
        ctx.get().setPacketHandled(true);
    }

}
