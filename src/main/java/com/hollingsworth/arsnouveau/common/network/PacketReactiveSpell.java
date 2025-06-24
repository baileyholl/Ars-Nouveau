package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.event.ReactiveEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PacketReactiveSpell extends AbstractPacket {


    public static final Type<PacketReactiveSpell> TYPE = new Type<>(ArsNouveau.prefix("reactive_spell"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketReactiveSpell> CODEC = StreamCodec.ofMember(PacketReactiveSpell::toBytes, PacketReactiveSpell::new);

    public PacketReactiveSpell() {
    }


    //Decoder
    public PacketReactiveSpell(RegistryFriendlyByteBuf buf) {
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        ReactiveEvents.castSpell(player, stack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
