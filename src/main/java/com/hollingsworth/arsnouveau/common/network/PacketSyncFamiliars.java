package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.FamiliarCap;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliarCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncFamiliars {
    CompoundNBT tag;
    //Decoder
    public PacketSyncFamiliars(PacketBuffer buf){
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeNbt(tag);
    }

    public PacketSyncFamiliars(CompoundNBT famCaps){
       this.tag = famCaps;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            PlayerEntity playerEntity = ArsNouveau.proxy.getPlayer();
            IFamiliarCap cap = FamiliarCap.getFamiliarCap(playerEntity).orElse(null);
            if(cap != null){
                cap.setUnlockedFamiliars(FamiliarCap.deserializeFamiliars(tag));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
