package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ScryBot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class PacketMoveScryBot extends AbstractPacket{
    public static final Type<PacketMoveScryBot> TYPE = new Type<>(ArsNouveau.prefix("move_bot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketMoveScryBot> CODEC = StreamCodec.ofMember(PacketMoveScryBot::encode, PacketMoveScryBot::decode);

    public float leftImpulse;
    public float forwardImpulse;
    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;
    public boolean jumping;
    public boolean shiftKeyDown;
    public int entityId;
    public float yRot;
    public float xRot;
    public PacketMoveScryBot(int entityId, float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left, boolean right, boolean jumping, boolean shiftKeyDown, float xRot, float yRot) {
        this.entityId = entityId;
        this.leftImpulse = leftImpulse;
        this.forwardImpulse = forwardImpulse;
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.jumping = jumping;
        this.shiftKeyDown = shiftKeyDown;
        this.xRot = xRot;
        this.yRot = yRot;
    }

    public static void encode(PacketMoveScryBot message, RegistryFriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeFloat(message.leftImpulse);
        buf.writeFloat(message.forwardImpulse);
        buf.writeBoolean(message.up);
        buf.writeBoolean(message.down);
        buf.writeBoolean(message.left);
        buf.writeBoolean(message.right);
        buf.writeBoolean(message.jumping);
        buf.writeBoolean(message.shiftKeyDown);
        buf.writeFloat(message.xRot);
        buf.writeFloat(message.yRot);
    }

    public static PacketMoveScryBot decode(RegistryFriendlyByteBuf buf) {
        return new PacketMoveScryBot(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readFloat(), buf.readFloat());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        Entity entity = player.level.getEntity(entityId);
        if (entity instanceof ScryBot scryBot) {
            scryBot.onMove(this);
        }
    }
}
