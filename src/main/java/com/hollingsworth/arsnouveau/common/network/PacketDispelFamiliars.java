package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.hollingsworth.arsnouveau.common.event.FamiliarEvents;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketDispelFamiliars {

    public PacketDispelFamiliars() {}

    //Decoder
    public PacketDispelFamiliars(FriendlyByteBuf buf) {
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                Entity owner = ctx.get().getSender();
                dispelForPlayer(owner);
            }
        });
        ctx.get().setPacketHandled(true);

    }

    public static boolean dispelForPlayer(Entity owner) {
        boolean removedFamiliar = false;
        for (FamiliarEntity familiarEntity : FamiliarEvents.getFamiliars(i -> i.getOwnerID() == null || i.getOwnerID().equals(owner.getUUID()))) {
            familiarEntity.remove(Entity.RemovalReason.DISCARDED);
            ParticleUtil.spawnPoof((ServerLevel) owner.level, familiarEntity.getThisEntity().blockPosition());
            removedFamiliar = true;
        }
        if (removedFamiliar) {
            PortUtil.sendMessage(owner, Component.translatable("ars_nouveau.removed_familiars"));
        }
        return removedFamiliar;
    }
}
