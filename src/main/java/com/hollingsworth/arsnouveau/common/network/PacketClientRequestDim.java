package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class PacketClientRequestDim extends AbstractPacket {
    public static final Type<PacketClientRequestDim> TYPE = new Type<>(ArsNouveau.prefix("request_planarium"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketClientRequestDim> CODEC = StreamCodec.ofMember(PacketClientRequestDim::toBytes, PacketClientRequestDim::new);

    public BlockPos pos;

    public PacketClientRequestDim(BlockPos pos) {
        this.pos = pos;
    }

    public PacketClientRequestDim(RegistryFriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        super.onServerReceived(minecraftServer, player);
        if (player.level.getBlockEntity(pos) instanceof PlanariumTile planariumTile && planariumTile.key != null) {
            StructureTemplate template = PlanariumTile.dimManager.getTemplate(planariumTile.key);
            if (template != null) {
                Networking.sendToPlayerClient(new PacketUpdateDimTile(pos, template), player);
            }
        }
    }
}
