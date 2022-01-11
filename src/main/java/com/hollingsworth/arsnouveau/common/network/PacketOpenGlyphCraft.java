package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenGlyphCraft {

    public PacketOpenGlyphCraft(FriendlyByteBuf buf){

    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){

    }

    public PacketOpenGlyphCraft(){

    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> GlyphUnlockMenu.open());
        ctx.get().setPacketHandled(true);
    }

}
