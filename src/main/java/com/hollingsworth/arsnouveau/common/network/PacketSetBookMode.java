package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PacketSetBookMode extends AbstractPacket{
    public static final Type<PacketSetBookMode> TYPE = new Type<>(ArsNouveau.prefix("set_book_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetBookMode> CODEC = StreamCodec.ofMember(PacketSetBookMode::toBytes, PacketSetBookMode::new);
    public CompoundTag tag;

    //Decoder
    public PacketSetBookMode(RegistryFriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public PacketSetBookMode(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = StackUtil.getHeldSpellbook(player);
        if (stack.getItem() instanceof SpellBook) {
            stack.setTag(tag);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
