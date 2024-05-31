package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketOneShotAnimation {
    final int x;
    final int y;
    final int z;
    final int arg;

    public PacketOneShotAnimation(int x, int y, int z, int arg) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.arg = arg;
    }

    public PacketOneShotAnimation(BlockPos pos, int arg) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.arg = arg;
    }

    public PacketOneShotAnimation(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.arg = 0;
    }

    public static PacketOneShotAnimation decode(FriendlyByteBuf buf) {
        return new PacketOneShotAnimation(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void encode(PacketOneShotAnimation msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
        buf.writeInt(msg.z);
        buf.writeInt(msg.arg);
    }

    public static class Handler {
        public static void handle(final PacketOneShotAnimation m, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    Minecraft mc = Minecraft.getInstance();
                    ClientLevel world = mc.level;
                    if (world.getBlockEntity(new BlockPos(m.x, m.y, m.z)) instanceof IAnimationListener) {
                        ((IAnimationListener) world.getBlockEntity(new BlockPos(m.x, m.y, m.z))).startAnimation(m.arg);
                    }
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
