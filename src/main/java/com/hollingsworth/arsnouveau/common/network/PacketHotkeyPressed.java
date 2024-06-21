package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketHotkeyPressed {

    public enum Key {
        NEXT,
        PREVIOUS
    }

    Key key;

    public PacketHotkeyPressed(Key key) {
        this.key = key;
    }

    //Decoder
    public PacketHotkeyPressed(FriendlyByteBuf buf) {
        this.key = Key.valueOf(buf.readUtf());
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(key.name());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Returns the hand holding an item with slots > 1, this only checks for NEXT/PREVIOUS slots for hotkeys.
                InteractionHand hand = StackUtil.getHeldCasterTool(player, (tool) -> tool.getSpellCaster().getMaxSlots() > 1);
                if (hand == null)
                    return;
                ItemStack stack = player.getItemInHand(hand);
                if (!(stack.getItem() instanceof ISpellHotkeyListener hotkeyListener)) {
                    return;
                }
                if (key == Key.NEXT) {
                    hotkeyListener.onNextKeyPressed(stack, player);
                } else if (key == Key.PREVIOUS) {
                    hotkeyListener.onPreviousKeyPressed(stack, player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
