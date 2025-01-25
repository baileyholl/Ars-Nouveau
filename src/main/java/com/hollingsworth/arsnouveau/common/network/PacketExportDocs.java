package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketExportDocs extends AbstractPacket{
    public static final Type<PacketExportDocs> TYPE = new Type<>(ArsNouveau.prefix("export_docs"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketExportDocs> CODEC = StreamCodec.ofMember(PacketExportDocs::toBytes, PacketExportDocs::new);

    String modid;
    public PacketExportDocs(String modid){
        this.modid = modid;
    }

    public PacketExportDocs(RegistryFriendlyByteBuf buf) {
        this.modid = buf.readUtf();
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(modid);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        DocExporter.export(modid);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
