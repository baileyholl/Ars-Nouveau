package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerDataCap;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncFamiliars {
    CompoundTag tag;
    //Decoder
    public PacketSyncFamiliars(FriendlyByteBuf buf){
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeNbt(tag);
    }

    public PacketSyncFamiliars(CompoundTag famCaps){
       this.tag = famCaps;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            Player playerEntity = ArsNouveau.proxy.getPlayer();
            IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(playerEntity).orElse(null);

            if(cap != null){
                cap.setUnlockedFamiliars(ANPlayerDataCap.deserialize(tag).familiars);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
