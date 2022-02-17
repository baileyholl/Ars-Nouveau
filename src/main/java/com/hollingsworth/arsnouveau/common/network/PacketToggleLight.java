package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleLight {
    boolean enabled;
    //Decoder
    public PacketToggleLight(FriendlyByteBuf buf){
        enabled = buf.readBoolean();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeBoolean(enabled);
    }

    public PacketToggleLight(boolean stack){
        this.enabled = stack;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            Config.DYNAMIC_LIGHTS_ENABLED.set(enabled);
            Config.DYNAMIC_LIGHTS_ENABLED.save();
        } );
        ctx.get().setPacketHandled(true);
    }

}
