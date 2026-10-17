package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.capability.ManaData;
import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketUpdateMana extends AbstractPacket {
    ManaData mana;

    @Deprecated(forRemoval = true)
    public PacketUpdateMana(CompoundTag tag) {
        this.mana = new ManaData();
        this.mana.setMaxMana(tag.getInt("max"));
        this.mana.setMana(tag.getDouble("current"));
        this.mana.setBookTier(tag.getInt("book_tier"));
        this.mana.setGlyphBonus(tag.getInt("glyph"));
        this.mana.setReservedMana(tag.getFloat("reserved"));
    }

    public PacketUpdateMana(ManaData mana) {
        this.mana = mana;
    }

    //Decoder
    public PacketUpdateMana(RegistryFriendlyByteBuf buf) {
        this.mana = CODEC.decode(buf).mana;
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        CODEC.encode(buf, this);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        player.setData(AttachmentsRegistry.MANA_ATTACHMENT, mana);
        var cap = CapabilityRegistry.getMana(ArsNouveau.proxy.getPlayer());
        if (cap != null) {
            cap.setManaData(mana);
        }
        //sync the client cache of reserved mana
        ClientInfo.reservedOverlayMana = mana.getReservedMana();
    }

    public static final Type<PacketUpdateMana> TYPE = new Type<>(ArsNouveau.prefix("update_mana"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateMana> CODEC = StreamCodec.composite(
            ManaData.STREAM_CODEC, p -> p.mana,
            PacketUpdateMana::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
