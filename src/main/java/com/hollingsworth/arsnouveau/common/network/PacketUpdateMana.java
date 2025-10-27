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
    CompoundTag tag;

    public PacketUpdateMana(CompoundTag tag) {
        this.tag = tag;
    }

    //Decoder
    public PacketUpdateMana(RegistryFriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ManaData data = new ManaData();
        data.deserializeNBT(player.registryAccess(), this.tag);
        player.setData(AttachmentsRegistry.MANA_ATTACHMENT, data);
        var cap = CapabilityRegistry.getMana(ArsNouveau.proxy.getPlayer());
        if (cap != null) {
            cap.setManaData(data);
        }
        //sync the client cache of reserved mana
        ClientInfo.reservedOverlayMana = data.getReservedMana();
    }

    public static final Type<PacketUpdateMana> TYPE = new Type<>(ArsNouveau.prefix("update_mana"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateMana> CODEC = StreamCodec.ofMember(PacketUpdateMana::toBytes, PacketUpdateMana::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
