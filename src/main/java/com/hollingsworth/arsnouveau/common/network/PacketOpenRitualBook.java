package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.gui.book.GuiRitualBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenRitualBook {
    public CompoundNBT tag;
    public String unlockedRituals;


    public PacketOpenRitualBook(CompoundNBT tag, String unlockedRituals){
        this.tag = tag;
        this.unlockedRituals = unlockedRituals;
    }

    //Decoder
    public PacketOpenRitualBook(PacketBuffer buf){
        tag = buf.readNbt();
        unlockedRituals = buf.readUtf(32767);
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeNbt(tag);
        buf.writeUtf(unlockedRituals);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> GuiRitualBook.open());
        ctx.get().setPacketHandled(true);
    }

}
