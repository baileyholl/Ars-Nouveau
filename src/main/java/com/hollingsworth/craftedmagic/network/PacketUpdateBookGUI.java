package com.hollingsworth.craftedmagic.network;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellBook;
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
            if(Minecraft.getInstance().currentScreen instanceof GuiSpellBook){
                ((GuiSpellBook) ArsNouveau.proxy.getMinecraft().currentScreen).spell_book_tag = tag;
            }else{
                System.out.println("Screen is not GuiSpellCreation!");
            }
        } );
        ctx.get().setPacketHandled(true);
    }

}
