package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class PacketMountCamera extends AbstractPacket {
    public static final Type<PacketMountCamera> TYPE = new Type<>(ArsNouveau.prefix("mount_camera"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketMountCamera> CODEC = StreamCodec.ofMember(PacketMountCamera::encode, PacketMountCamera::decode);
    private BlockPos pos;

    public PacketMountCamera() {
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {

    }

    public PacketMountCamera(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(PacketMountCamera message, RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
    }

    public static PacketMountCamera decode(RegistryFriendlyByteBuf buf) {
        return new PacketMountCamera(buf.readBlockPos());
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        Level level = player.level;
//        ChunkPos chunkPos = new ChunkPos(pos);
//        int viewDistance = player.server.getPlayerList().getViewDistance();
//        for (int x = chunkPos.x - viewDistance; x <= chunkPos.x + viewDistance; x++) {
//            for (int z = chunkPos.z - viewDistance; z <= chunkPos.z + viewDistance; z++) {
//                ArsNouveau.ticketController.forceChunk((ServerLevel) player.level, player, x, z, true, true);
//            }
//        }

        if (level.getBlockEntity(pos) instanceof ICameraMountable mountable) {
            mountable.mountCamera(level, pos, player);
            return;
        }
        PortUtil.sendMessage(player, Component.translatable("ars_nouveau.camera.not_loaded"));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
