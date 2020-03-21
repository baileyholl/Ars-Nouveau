package com.hollingsworth.arsnouveau.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.GuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateBookGUI {

    public CompoundNBT tag;
    //Decoder
    public PacketUpdateBookGUI(PacketBuffer buf){
        tag = buf.readCompoundTag();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(tag);
    }

    public PacketUpdateBookGUI(CompoundNBT tag){
        this.tag = tag;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(Minecraft.getInstance().currentScreen instanceof GuiSpellBook)
                ((GuiSpellBook) ArsNouveau.proxy.getMinecraft().currentScreen).spell_book_tag = tag;
        } );
        ctx.get().setPacketHandled(true);
    }

}
