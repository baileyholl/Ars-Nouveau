package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateMana {

    public double mana;

    public int maxMana;

    public int glyphBonus;

    public int tierBonus;
    //Decoder
    public PacketUpdateMana(PacketBuffer buf){
        mana = buf.readDouble();
        maxMana = buf.readInt();
        glyphBonus = buf.readInt();
        tierBonus = buf.readInt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeDouble(mana);
        buf.writeInt(maxMana);
        buf.writeInt(glyphBonus);
        buf.writeInt(tierBonus);
    }

    public PacketUpdateMana(double mana, int maxMana, int glyphBonus, int tierBonus){
        this.mana = mana;
        this.maxMana = maxMana;
        this.glyphBonus = glyphBonus;
        this.tierBonus = tierBonus;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ArsNouveau.proxy.getPlayer() == null)
                return;
            ManaCapability.getMana(ArsNouveau.proxy.getPlayer()).ifPresent(mana ->{
                mana.setMana(this.mana);
                mana.setMaxMana(this.maxMana);
                mana.setGlyphBonus(this.glyphBonus);
                mana.setBookTier(this.tierBonus);
            });
        } );
        ctx.get().setPacketHandled(true);
    }
}
