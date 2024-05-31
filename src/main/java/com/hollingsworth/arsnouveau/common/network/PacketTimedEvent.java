package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.event.ChimeraSummonEvent;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.common.event.timed.EruptionEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketTimedEvent {

    CompoundTag tag;

    //Decoder
    public PacketTimedEvent(FriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public PacketTimedEvent(CompoundTag tag) {
        this.tag = tag;
    }

    public PacketTimedEvent(ITimedEvent event) {
        this.tag = new CompoundTag();
        event.serialize(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (!methodMap.containsKey(tag.getString("id")))
                throw new IllegalStateException("No event found for ID or ID missing");
            methodMap.get(tag.getString("id")).apply(tag);
        });
        ctx.get().setPacketHandled(true);
    }

    public static Map<String, Function<CompoundTag, Void>> methodMap = new HashMap<>();

    static {
        methodMap.put(ChimeraSummonEvent.ID, (nbt) -> ChimeraSummonEvent.get(nbt).onPacketHandled());
        methodMap.put(EruptionEvent.ID, (nbt) -> EruptionEvent.get(nbt).onPacketHandled());
    }
}
