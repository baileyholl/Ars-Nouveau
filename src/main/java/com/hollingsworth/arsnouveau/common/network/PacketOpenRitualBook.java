package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.client.gui.book.GuiRitualBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketOpenRitualBook {
    public CompoundNBT tag;
    public List<String> unlockedRituals;
    public boolean isMainhand;

    public PacketOpenRitualBook(CompoundNBT tag, List<String> unlockedRituals, boolean isMainhand){
        this.tag = tag;
        this.unlockedRituals = unlockedRituals;
        this.isMainhand = isMainhand;
    }

    //Decoder
    public PacketOpenRitualBook(PacketBuffer buf){
        tag = buf.readNbt();
        int num = buf.readInt();
        unlockedRituals = new ArrayList<>();
        for(int i = 0; i < num; i++){
            unlockedRituals.add(buf.readUtf(32767));
        }
        this.isMainhand = buf.readBoolean();

    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeNbt(tag);
        buf.writeInt(unlockedRituals.size());
        for (String unlockedRitual : unlockedRituals) {
            buf.writeUtf(unlockedRitual);
        }
        buf.writeBoolean(isMainhand);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> GuiRitualBook.open(ArsNouveauAPI.getInstance(), unlockedRituals, isMainhand));
        ctx.get().setPacketHandled(true);
    }

}
