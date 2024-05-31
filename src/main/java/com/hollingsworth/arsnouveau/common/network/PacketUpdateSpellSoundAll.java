package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.function.Supplier;

public class PacketUpdateSpellSoundAll extends PacketSetSound {
    public PacketUpdateSpellSoundAll(int castSlot, ConfiguredSpellSound sound, boolean mainHand) {
        super(castSlot, sound, mainHand);
    }

    public PacketUpdateSpellSoundAll(FriendlyByteBuf buf) {
        super(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                ItemStack stack = ctx.get().getSender().getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                if (stack.getItem() instanceof SpellBook) {
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    for (int i = 0; i < caster.getMaxSlots(); i++) {
                        caster.setSound(sound, i);
                    }
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketUpdateBookGUI(stack));
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()),
                            new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
