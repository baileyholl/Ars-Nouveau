package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.FamiliarSummonEvent;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;

public class PacketSummonFamiliar extends AbstractPacket{

    public static final Type<PacketSummonFamiliar> TYPE = new Type<>(ArsNouveau.prefix("summon_familiar"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSummonFamiliar> CODEC = StreamCodec.ofMember(PacketSummonFamiliar::toBytes, PacketSummonFamiliar::new);


    ResourceLocation familiarID;

    public PacketSummonFamiliar(ResourceLocation id) {
        this.familiarID = id;
    }

    //Decoder
    public PacketSummonFamiliar(RegistryFriendlyByteBuf buf) {
        familiarID = buf.readResourceLocation();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(familiarID);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer owner) {
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(owner).orElse(null);
        if (cap == null)
            return;

        if(owner == null)
            return;

        IFamiliar familiarEntity = cap.getFamiliarData(familiarID).getEntity(owner.level);
        familiarEntity.setOwnerID(owner.getUUID());
        familiarEntity.getThisEntity().setPos(owner.getX(), owner.getY(), owner.getZ());

        FamiliarSummonEvent summonEvent = new FamiliarSummonEvent(familiarEntity.getThisEntity(), owner);
        NeoForge.EVENT_BUS.post(summonEvent);

        if (!summonEvent.isCanceled()) {
            owner.level.addFreshEntity(familiarEntity.getThisEntity());
            ParticleUtil.spawnPoof((ServerLevel) owner.level, familiarEntity.getThisEntity().blockPosition());
            cap.setLastSummonedFamiliar(familiarID);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
