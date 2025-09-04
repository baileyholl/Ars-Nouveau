package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketOneShotAnimation extends AbstractPacket {
    public static final Type<PacketOneShotAnimation> TYPE = new Type<>(ArsNouveau.prefix("one_shot_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOneShotAnimation> CODEC = StreamCodec.ofMember(PacketOneShotAnimation::encode, PacketOneShotAnimation::decode);

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

    public static PacketOneShotAnimation decode(RegistryFriendlyByteBuf buf) {
        return new PacketOneShotAnimation(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void encode(PacketOneShotAnimation msg, RegistryFriendlyByteBuf buf) {
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
        buf.writeInt(msg.z);
        buf.writeInt(msg.arg);
    }


    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {

    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientLevel world = minecraft.level;
        if (world.getBlockEntity(new BlockPos(x, y, z)) instanceof IAnimationListener) {
            ((IAnimationListener) world.getBlockEntity(new BlockPos(x, y, z))).startAnimation(arg);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
