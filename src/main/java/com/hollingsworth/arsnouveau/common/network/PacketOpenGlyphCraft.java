package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketOpenGlyphCraft extends AbstractPacket{
    public static final Type<PacketOpenGlyphCraft> TYPE = new Type<>(ArsNouveau.prefix("open_glyph_craft"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenGlyphCraft> CODEC = StreamCodec.ofMember(PacketOpenGlyphCraft::toBytes, PacketOpenGlyphCraft::new);
    BlockPos scribePos;

    public PacketOpenGlyphCraft(RegistryFriendlyByteBuf buf) {
        this.scribePos = buf.readBlockPos();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(scribePos);
    }

    public PacketOpenGlyphCraft(BlockPos scribesPos) {
        this.scribePos = scribesPos;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        GlyphUnlockMenu.open(scribePos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
