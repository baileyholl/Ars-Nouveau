package com.hollingsworth.craftedmagic.network;

import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateMana {

    public int mana;
    //Decoder
    public PacketUpdateMana(PacketBuffer buf){
        mana = buf.readInt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeInt(mana);
    }

    public PacketUpdateMana(int mana){
        this.mana = mana;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ManaCapability.getMana(Minecraft.getInstance().player).ifPresent(mana ->{
                mana.setMana(this.mana);
            });
        } );
        ctx.get().setPacketHandled(true);
    }
}
