package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.FadeLightTimedEvent;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketAddFadingLight {
    final double x;
    final double y;
    final double z;

    public PacketAddFadingLight(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketAddFadingLight(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public static PacketAddFadingLight decode(FriendlyByteBuf buf) {
        return new PacketAddFadingLight(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void encode(PacketAddFadingLight msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
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
                    if (LightManager.shouldUpdateDynamicLight())
                        EventQueue.getClientQueue().addEvent(new FadeLightTimedEvent(Minecraft.getInstance().level, new Vec3(m.x, m.y, m.z), Config.TOUCH_LIGHT_DURATION.get(), Config.TOUCH_LIGHT_LUMINANCE.get()));
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
