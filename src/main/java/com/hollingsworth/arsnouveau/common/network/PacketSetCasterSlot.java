package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PacketSetCasterSlot extends AbstractPacket{
    public static final Type<PacketSetCasterSlot> TYPE = new Type<>(ArsNouveau.prefix("set_book_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetCasterSlot> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            s -> s.slot,
            PacketSetCasterSlot::new
    );

    public int slot;

    public PacketSetCasterSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = StackUtil.getHeldSpellbook(player);
        SpellCaster caster = SpellCasterRegistry.from(stack);
        if(caster != null){
            caster.setCurrentSlot(slot).saveToStack(stack);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
