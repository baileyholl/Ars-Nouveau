package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.FadeLightTimedEvent;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PacketAddFadingLight extends AbstractPacket{
    public static final Type<PacketAddFadingLight> TYPE = new Type(ArsNouveau.prefix("add_fading_light"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAddFadingLight> CODEC = StreamCodec.ofMember(PacketAddFadingLight::toBytes, PacketAddFadingLight::new);
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

    public PacketAddFadingLight(RegistryFriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (LightManager.shouldUpdateDynamicLight())
            EventQueue.getClientQueue().addEvent(new FadeLightTimedEvent(Minecraft.getInstance().level, new Vec3(x,y, z), Config.TOUCH_LIGHT_DURATION.get(), Config.TOUCH_LIGHT_LUMINANCE.get()));
    }
}
