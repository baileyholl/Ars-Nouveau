package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketUpdateParticleTimeline extends AbstractPacket{

    int castSlot;
    TimelineMap color;
    boolean mainHand;

    public PacketUpdateParticleTimeline(int slot, TimelineMap color, boolean mainHand) {
        this.castSlot = slot;
        this.color = color;
        this.mainHand = mainHand;
    }

    //Decoder
    public PacketUpdateParticleTimeline(RegistryFriendlyByteBuf buf) {
        castSlot = buf.readInt();
        color = TimelineMap.STREAM.decode(buf);
        mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(castSlot);
        TimelineMap.STREAM.encode(buf, color);
        buf.writeBoolean(mainHand);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof SpellBook) {
            AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
            if(caster != null) {
                caster.setParticles(color, castSlot).saveToStack(stack);
            }
        }
    }

    public static final Type<PacketUpdateParticleTimeline> TYPE = new Type<>(ArsNouveau.prefix("update_particle_timeline"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateParticleTimeline> CODEC = StreamCodec.ofMember(PacketUpdateParticleTimeline::toBytes, PacketUpdateParticleTimeline::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
