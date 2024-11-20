package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketInitDocs extends AbstractPacket{
    public static final Type<PacketInitDocs> TYPE = new Type<>(ArsNouveau.prefix("init_docs"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketInitDocs> CODEC = StreamCodec.ofMember(PacketInitDocs::toBytes, PacketInitDocs::new);


    public PacketInitDocs(){
    }

    public PacketInitDocs(RegistryFriendlyByteBuf buf) {
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        Documentation.initOnWorldReload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
