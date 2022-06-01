package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMountCamera {
    private BlockPos pos;

    public PacketMountCamera() {
    }

    public PacketMountCamera(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(PacketMountCamera message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
    }

    public static PacketMountCamera decode(FriendlyByteBuf buf) {
        PacketMountCamera message = new PacketMountCamera();
        message.pos = buf.readBlockPos();
        return message;
    }

    public static void onMessage(PacketMountCamera message, Supplier<NetworkEvent.Context> ctx) {
        (ctx.get()).enqueueWork(() -> {
            // Resolve serverside
            BlockPos pos = message.pos;
            ServerPlayer player = (ctx.get()).getSender();
            Level level = player.level;

            if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof ICameraMountable mountable) {
                mountable.mountCamera(level, pos, player);
                return;
            }
            PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.camera.not_loaded"));
        });
        (ctx.get()).setPacketHandled(true);
    }
}
