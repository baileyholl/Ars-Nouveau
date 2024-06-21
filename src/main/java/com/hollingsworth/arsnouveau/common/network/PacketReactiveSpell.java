package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.event.ReactiveEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketReactiveSpell {

    public PacketReactiveSpell() {
    }


    //Decoder
    public PacketReactiveSpell(FriendlyByteBuf buf) {
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayerEntity = ctx.get().getSender();
            if (serverPlayerEntity != null) {
                ItemStack stack = serverPlayerEntity.getMainHandItem();
                ReactiveEvents.castSpell(ctx.get().getSender(), stack);
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
