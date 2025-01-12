package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ChimeraSummonEvent;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.common.event.timed.EruptionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PacketTimedEvent extends AbstractPacket{

    CompoundTag tag;

    //Decoder
    public PacketTimedEvent(RegistryFriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public PacketTimedEvent(CompoundTag tag) {
        this.tag = tag;
    }

    public PacketTimedEvent(ITimedEvent event) {
        this.tag = new CompoundTag();
        event.serialize(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (!methodMap.containsKey(tag.getString("id")))
            throw new IllegalStateException("No event found for ID or ID missing");
        methodMap.get(tag.getString("id")).apply(tag);
    }

    public static final Type<PacketTimedEvent> TYPE = new Type<>(ArsNouveau.prefix("timed_event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTimedEvent> CODEC = StreamCodec.ofMember(PacketTimedEvent::toBytes, PacketTimedEvent::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static Map<String, Function<CompoundTag, Void>> methodMap = new HashMap<>();

    static {
        methodMap.put(ChimeraSummonEvent.ID, (nbt) -> ChimeraSummonEvent.get(nbt).onPacketHandled());
        methodMap.put(EruptionEvent.ID, (nbt) -> EruptionEvent.get(nbt).onPacketHandled());
    }
}
