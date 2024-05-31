package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketOpenGlyphCraft {
    BlockPos scribePos;

    public PacketOpenGlyphCraft(FriendlyByteBuf buf) {
        this.scribePos = buf.readBlockPos();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(scribePos);
    }

    public PacketOpenGlyphCraft(BlockPos scribesPos) {
        this.scribePos = scribesPos;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->{
            GlyphUnlockMenu.open(scribePos);
        });
        ctx.get().setPacketHandled(true);
    }

}
