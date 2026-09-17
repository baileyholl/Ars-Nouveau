package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import com.hollingsworth.arsnouveau.common.event.FamiliarEvents;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class PacketDispelFamiliars extends AbstractPacket {
    public static final Type<PacketDispelFamiliars> TYPE = new Type<>(ArsNouveau.prefix("dispel_familiars"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketDispelFamiliars> CODEC = StreamCodec.ofMember(PacketDispelFamiliars::toBytes, PacketDispelFamiliars::new);


    public PacketDispelFamiliars() {
    }

    //Decoder
    public PacketDispelFamiliars(RegistryFriendlyByteBuf buf) {
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        dispelForPlayer(player);
    }

    public static boolean dispelForPlayer(Entity owner) {
        boolean removedFamiliar = false;
        if (owner instanceof ServerPlayer p) {
            p.removeEntitiesOnShoulder();
        }
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

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
