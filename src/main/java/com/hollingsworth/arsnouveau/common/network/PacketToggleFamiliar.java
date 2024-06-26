package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.FamiliarSummonEvent;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.capability.FamiliarData;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;

public class PacketToggleFamiliar extends AbstractPacket{


    public PacketToggleFamiliar() {}

    //Decoder
    public PacketToggleFamiliar(RegistryFriendlyByteBuf buf) {
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer owner) {
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(owner).orElse(null);
        if (cap == null)
            return;

        if(PacketDispelFamiliars.dispelForPlayer(owner)){
            return;
        }

        FamiliarData lastSummoned = cap.getLastSummonedFamiliar();
        if(lastSummoned == null)
            return;
        IFamiliar familiarEntity = lastSummoned.getEntity(owner.level);
        familiarEntity.setOwnerID(owner.getUUID());
        familiarEntity.getThisEntity().setPos(owner.getX(), owner.getY(), owner.getZ());

        FamiliarSummonEvent summonEvent = new FamiliarSummonEvent(familiarEntity.getThisEntity(), owner);
        NeoForge.EVENT_BUS.post(summonEvent);

        if (!summonEvent.isCanceled()) {
            owner.level.addFreshEntity(familiarEntity.getThisEntity());
            ParticleUtil.spawnPoof((ServerLevel) owner.level, familiarEntity.getThisEntity().blockPosition());
        }
    }

    public static final Type<PacketToggleFamiliar> TYPE = new Type<>(ArsNouveau.prefix("toggle_familiar"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketToggleFamiliar> CODEC = StreamCodec.ofMember(PacketToggleFamiliar::toBytes, PacketToggleFamiliar::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
