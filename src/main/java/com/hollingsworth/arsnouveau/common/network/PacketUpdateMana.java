package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketUpdateMana {

    public double mana;

    public int maxMana;

    public int glyphBonus;

    public int tierBonus;
    public float reserved;

    //Decoder
    public PacketUpdateMana(FriendlyByteBuf buf) {
        mana = buf.readDouble();
        maxMana = buf.readInt();
        glyphBonus = buf.readInt();
        tierBonus = buf.readInt();
        reserved = buf.readFloat();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(mana);
        buf.writeInt(maxMana);
        buf.writeInt(glyphBonus);
        buf.writeInt(tierBonus);
        buf.writeFloat(reserved);
    }

    public PacketUpdateMana(double mana, int maxMana, int glyphBonus, int tierBonus, float reserved) {
        this.mana = mana;
        this.maxMana = maxMana;
        this.glyphBonus = glyphBonus;
        this.tierBonus = tierBonus;
        this.reserved = reserved;
    }

    public PacketUpdateMana(double mana, int maxMana, int glyphBonus, int tierBonus) {
        this.mana = mana;
        this.maxMana = maxMana;
        this.glyphBonus = glyphBonus;
        this.tierBonus = tierBonus;
        this.reserved = -1;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ArsNouveau.proxy.getPlayer() == null)
                return;
            CapabilityRegistry.getMana(ArsNouveau.proxy.getPlayer()).ifPresent(mana -> {
                mana.setMana(this.mana);
                mana.setMaxMana(this.maxMana);
                mana.setGlyphBonus(this.glyphBonus);
                mana.setBookTier(this.tierBonus);
            });
            //sync the client cache of reserved mana
            if (ctx.get().getDirection().getReceptionSide().isClient() && reserved != -1)
                ClientInfo.reservedOverlayMana = reserved;
        });
        ctx.get().setPacketHandled(true);
    }
}
