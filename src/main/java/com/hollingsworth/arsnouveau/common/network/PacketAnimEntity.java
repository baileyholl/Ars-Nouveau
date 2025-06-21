package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PacketAnimEntity extends AbstractPacket {
    public static final Type<PacketAnimEntity> TYPE = new Type<>(ArsNouveau.prefix("anim_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAnimEntity> CODEC = StreamCodec.ofMember(PacketAnimEntity::toBytes, PacketAnimEntity::new);

    int entityID;
    int anim;

    public PacketAnimEntity(Entity entity) {
        this.entityID = entity.getId();
        this.anim = 0;
    }

    public PacketAnimEntity(int entityID) {
        this.entityID = entityID;
        this.anim = 0;
    }

    public PacketAnimEntity(int entityID, int anim) {
        this.entityID = entityID;
        this.anim = anim;
    }

    public PacketAnimEntity(RegistryFriendlyByteBuf buf) {
        entityID = buf.readInt();
        anim = buf.readInt();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeInt(anim);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientLevel world = minecraft.level;
        if (world.getEntity(entityID) instanceof IAnimationListener animationListener) {
            animationListener.startAnimation(anim);
        }
    }
}
