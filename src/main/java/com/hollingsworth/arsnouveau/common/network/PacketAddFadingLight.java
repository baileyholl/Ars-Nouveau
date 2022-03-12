package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.FadeLightTimedEvent;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAddFadingLight {
    final double x;
    final double y;
    final double z;
    final int duration;
    final int luminance;


    public PacketAddFadingLight(double x, double y, double z, int duration, int luminance){
        this.x = x;
        this.y = y;
        this.z = z;
        this.duration = duration;
        this.luminance = luminance;
    }

    public PacketAddFadingLight(BlockPos pos, int duration, int luminance){
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.duration = duration;
        this.luminance = luminance;
    }

    public static PacketAddFadingLight decode(FriendlyByteBuf buf) {
        return new PacketAddFadingLight(buf.readDouble(),buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readInt());
    }

    public static void encode(PacketAddFadingLight msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeInt(msg.duration);
        buf.writeInt(msg.luminance);
    }

    public static class Handler {
        public static void handle(final PacketAddFadingLight m, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(LightManager.shouldUpdateDynamicLight())
                        EventQueue.getClientQueue().addEvent(new FadeLightTimedEvent(Minecraft.getInstance().level, new Vec3(m.x, m.y, m.z), m.duration, m.luminance));
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
