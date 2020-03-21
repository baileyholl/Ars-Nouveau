package com.hollingsworth.arsnouveau.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.capability.ManaCapability;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateMana {

    public int mana;

    public int maxMana;
    //Decoder
    public PacketUpdateMana(PacketBuffer buf){
        mana = buf.readInt();
        maxMana = buf.readInt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeInt(mana);
        buf.writeInt(maxMana);
    }

    public PacketUpdateMana(int mana, int maxMana){
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ManaCapability.getMana(ArsNouveau.proxy.getPlayer()).ifPresent(mana ->{
                mana.setMana(this.mana);
                mana.setMaxMana(this.maxMana);
            });
        } );
        ctx.get().setPacketHandled(true);
    }
}
