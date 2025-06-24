package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PacketWarpPosition extends AbstractPacket {
    private final int entityID;
    double x;
    double y;
    double z;
    float xRot;
    float yRot;

    public PacketWarpPosition(Entity entity, double x, double y, double z) {
        this.entityID = entity.getId();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketWarpPosition(int id, double x, double y, double z, float xRot, float yRot) {
        this.entityID = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.yRot = yRot;
    }

    public PacketWarpPosition(RegistryFriendlyByteBuf buf) {
        entityID = buf.readInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        xRot = buf.readFloat();
        yRot = buf.readFloat();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(xRot);
        buf.writeFloat(yRot);
    }

    @Override
    public void onClientReceived(Minecraft mc, Player player) {
        ClientLevel world = mc.level;
        Entity e = world.getEntity(entityID);
        if (e == null)
            return;
        e.setPos(x, y, z);
        e.setXRot(xRot);
        e.setYRot(yRot);
    }

    public static final Type<PacketWarpPosition> TYPE = new Type<>(ArsNouveau.prefix("warp_position"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketWarpPosition> CODEC = StreamCodec.ofMember(PacketWarpPosition::toBytes, PacketWarpPosition::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
