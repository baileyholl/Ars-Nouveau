package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenSpellBook {
    public CompoundNBT tag;
    public int tier;
    public String unlockedSpells;

    //Decoder
    public PacketOpenSpellBook(PacketBuffer buf){
        tag = buf.readCompoundTag();
        tier = buf.readInt();
        unlockedSpells = buf.readString(32767);
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(tag);
        buf.writeInt(tier);
        buf.writeString(unlockedSpells);
    }

    public PacketOpenSpellBook(CompoundNBT tag, int tier, String unlockedSpells){
        this.tag = tag;
        this.tier = tier;
        this.unlockedSpells = unlockedSpells;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> GuiSpellBook.open(ArsNouveauAPI.getInstance(), tag, tier, unlockedSpells));
        ctx.get().setPacketHandled(true);
    }

}
