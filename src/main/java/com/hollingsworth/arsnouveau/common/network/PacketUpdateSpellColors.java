package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
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

public class PacketUpdateSpellColors extends AbstractPacket{

    int castSlot;
    ParticleColor color;
    boolean mainHand;

    public PacketUpdateSpellColors(int slot, ParticleColor color, boolean mainHand) {
        this.castSlot = slot;
        this.color = color;
        this.mainHand = mainHand;
    }

    //Decoder
    public PacketUpdateSpellColors(RegistryFriendlyByteBuf buf) {
        castSlot = buf.readInt();
        color = ParticleColorRegistry.from(buf.readNbt());
        mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(castSlot);
        buf.writeNbt(color.serialize());
        buf.writeBoolean(mainHand);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof SpellBook) {
            AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
            caster.setColor(color, castSlot).setCurrentSlot(castSlot).saveToStack(stack);
            Networking.sendToPlayerClient(new PacketUpdateBookGUI(stack), player);
            Networking.sendToPlayerClient( new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND), player);
        }
    }

    public static final Type<PacketUpdateSpellColors> TYPE = new Type<>(ArsNouveau.prefix("update_spell_colors"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateSpellColors> CODEC = StreamCodec.ofMember(PacketUpdateSpellColors::toBytes, PacketUpdateSpellColors::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
