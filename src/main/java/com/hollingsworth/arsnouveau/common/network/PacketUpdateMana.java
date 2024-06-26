package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketUpdateMana extends AbstractPacket{

    public double mana;

    public int maxMana;

    public int glyphBonus;

    public int tierBonus;
    public float reserved;

    //Decoder
    public PacketUpdateMana(RegistryFriendlyByteBuf buf) {
        mana = buf.readDouble();
        maxMana = buf.readInt();
        glyphBonus = buf.readInt();
        tierBonus = buf.readInt();
        reserved = buf.readFloat();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
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

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        var cap = CapabilityRegistry.getMana(ArsNouveau.proxy.getPlayer()).orElse(null);
        if(cap != null){
            cap.setMana(this.mana);
            cap.setMaxMana(this.maxMana);
            cap.setGlyphBonus(this.glyphBonus);
            cap.setBookTier(this.tierBonus);
        }
        //sync the client cache of reserved mana
        ClientInfo.reservedOverlayMana = reserved;
    }

    public static final Type<PacketUpdateMana> TYPE = new Type<>(ArsNouveau.prefix("update_mana"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateMana> CODEC = StreamCodec.ofMember(PacketUpdateMana::toBytes, PacketUpdateMana::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
