package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketHotkeyPressed extends AbstractPacket {
    public static final Type<PacketHotkeyPressed> TYPE = new Type<>(ArsNouveau.prefix("hotkey_pressed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketHotkeyPressed> CODEC = StreamCodec.ofMember(PacketHotkeyPressed::toBytes, PacketHotkeyPressed::new);

    public enum Key {
        NEXT,
        PREVIOUS
    }

    Key key;

    public PacketHotkeyPressed(Key key) {
        this.key = key;
    }

    //Decoder
    public PacketHotkeyPressed(RegistryFriendlyByteBuf buf) {
        this.key = Key.valueOf(buf.readUtf());
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(key.name());
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        // Returns the hand holding an item with slots > 1, this only checks for NEXT/PREVIOUS slots for hotkeys.
        InteractionHand hand = StackUtil.getHeldCasterTool(player, (tool) -> tool.getMaxSlots() > 1);
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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
