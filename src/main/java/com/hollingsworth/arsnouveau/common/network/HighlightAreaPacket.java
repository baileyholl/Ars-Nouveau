package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class HighlightAreaPacket extends AbstractPacket {
    public static final Type<HighlightAreaPacket> TYPE = new Type<>(ArsNouveau.prefix("highlight_area"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HighlightAreaPacket> CODEC = StreamCodec.ofMember(HighlightAreaPacket::toBytes, HighlightAreaPacket::decode);

    public List<ColorPos> colorPos;
    public int ticks;

    public HighlightAreaPacket(List<ColorPos> colorPos, int ticks) {
        this.colorPos = colorPos;
        this.ticks = ticks;
    }

    public static HighlightAreaPacket decode(RegistryFriendlyByteBuf buf) {
        HighlightAreaPacket packet = new HighlightAreaPacket(new ArrayList<>(), 0);
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            packet.colorPos.add(ColorPos.fromTag(buf.readNbt()));
        }
        packet.ticks = buf.readInt();
        return packet;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(colorPos.size());
        for (ColorPos pos : colorPos) {
            buf.writeNbt(pos.toTag());
        }
        buf.writeInt(ticks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientInfo.highlightPosition(colorPos, ticks);
    }
}
