package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketUpdateSpellColorAll extends PacketUpdateSpellColors {
    public PacketUpdateSpellColorAll(int slot, ParticleColor color, boolean mainHand) {
        super(slot, color, mainHand);
    }

    public PacketUpdateSpellColorAll(RegistryFriendlyByteBuf buf) {
        super(buf);
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        super.toBytes(buf);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof SpellBook) {
            AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
            for (int i = 0; i < caster.getMaxSlots(); i++) {
                caster = caster.setColor(color, i);
            }
            caster.setCurrentSlot(castSlot).saveToStack(stack);
            Networking.sendToPlayerClient(new PacketUpdateBookGUI(stack), player);
            Networking.sendToPlayerClient(new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND), player);

        }
    }
    public static final Type<PacketUpdateSpellColorAll> TYPE = new Type<>(ArsNouveau.prefix("update_spell_color_all"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateSpellColorAll> CODEC = StreamCodec.ofMember(PacketUpdateSpellColorAll::toBytes, PacketUpdateSpellColorAll::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
