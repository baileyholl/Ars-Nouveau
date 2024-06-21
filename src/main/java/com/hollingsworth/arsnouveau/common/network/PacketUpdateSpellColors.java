package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.particle.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.function.Supplier;

public class PacketUpdateSpellColors {

    int castSlot;
    ParticleColor color;
    boolean mainHand;

    public PacketUpdateSpellColors(int slot, ParticleColor color, boolean mainHand) {
        this.castSlot = slot;
        this.color = color;
        this.mainHand = mainHand;
    }

    //Decoder
    public PacketUpdateSpellColors(FriendlyByteBuf buf) {
        castSlot = buf.readInt();
        color = ParticleColorRegistry.from(buf.readNbt());
        mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(castSlot);
        buf.writeNbt(color.serialize());
        buf.writeBoolean(mainHand);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                ItemStack stack = ctx.get().getSender().getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                if (stack.getItem() instanceof SpellBook) {
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    caster.setColor(color, castSlot);
                    caster.setCurrentSlot(castSlot);
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketUpdateBookGUI(stack));
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()),
                            new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND));

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
