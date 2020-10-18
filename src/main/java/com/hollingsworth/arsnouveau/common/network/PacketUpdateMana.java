package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateMana {

    public int mana;

    public int maxMana;

    public int glyphBonus;

    public int tierBonus;
    //Decoder
    public PacketUpdateMana(PacketBuffer buf){
        mana = buf.readInt();
        maxMana = buf.readInt();
        glyphBonus = buf.readInt();
        tierBonus = buf.readInt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeInt(mana);
        buf.writeInt(maxMana);
        buf.writeInt(glyphBonus);
        buf.writeInt(tierBonus);
    }

    public PacketUpdateMana(int mana, int maxMana, int glyphBonus, int tierBonus){
        this.mana = mana;
        this.maxMana = maxMana;
        this.glyphBonus = glyphBonus;
        this.tierBonus = tierBonus;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
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
