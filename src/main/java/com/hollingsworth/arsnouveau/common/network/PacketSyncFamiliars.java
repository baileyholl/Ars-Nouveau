package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.FamiliarCap;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliarCap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

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
            IFamiliarCap cap = FamiliarCap.getFamiliarCap(playerEntity).orElse(null);
            if(cap != null){
                cap.setUnlockedFamiliars(FamiliarCap.deserializeFamiliars(tag));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
