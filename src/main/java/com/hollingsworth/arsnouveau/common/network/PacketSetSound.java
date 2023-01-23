package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketSetSound {

    int castSlot;
    ConfiguredSpellSound sound;
    boolean mainHand;
    public PacketSetSound(int castSlot, ConfiguredSpellSound sound, boolean mainHand) {
        this.castSlot = castSlot;
        this.sound = sound;
        this.mainHand = mainHand;
    }

    //Decoder
    public PacketSetSound(FriendlyByteBuf buf) {
        castSlot = buf.readInt();
        CompoundTag tag = buf.readNbt();
        sound = tag == null ? ConfiguredSpellSound.DEFAULT : ConfiguredSpellSound.fromTag(tag);
        mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(castSlot);
        buf.writeNbt(sound.serialize());
        buf.writeBoolean(mainHand);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                ItemStack stack = ctx.get().getSender().getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                if (stack.getItem() instanceof SpellBook) {
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    caster.setSound(sound, castSlot);
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketUpdateBookGUI(stack));
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()),
                            new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
