package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.event.FamiliarSummonEvent;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketSummonFamiliar {

    ResourceLocation familiarID;

    public PacketSummonFamiliar(ResourceLocation id) {
        this.familiarID = id;
    }

    //Decoder
    public PacketSummonFamiliar(FriendlyByteBuf buf) {
        familiarID = buf.readResourceLocation();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(familiarID);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(ctx.get().getSender()).orElse(null);
                if (cap == null)
                    return;
                Entity owner = ctx.get().getSender();
                if(owner == null)
                    return;

                IFamiliar familiarEntity = cap.getFamiliarData(familiarID).getEntity(ctx.get().getSender().level);
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
        });
        ctx.get().setPacketHandled(true);

    }
}
