package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketSetSound extends AbstractPacket{
    public static final Type<PacketSetSound> TYPE = new Type<>(ArsNouveau.prefix("set_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetSound> CODEC = StreamCodec.ofMember(PacketSetSound::toBytes, PacketSetSound::new);
    int castSlot;
    ConfiguredSpellSound sound;
    boolean mainHand;
    public PacketSetSound(int castSlot, ConfiguredSpellSound sound, boolean mainHand) {
        this.castSlot = castSlot;
        this.sound = sound;
        this.mainHand = mainHand;
    }

    //Decoder
    public PacketSetSound(RegistryFriendlyByteBuf buf) {
        castSlot = buf.readInt();
        CompoundTag tag = buf.readNbt();
        sound = tag == null ? ConfiguredSpellSound.DEFAULT : ANCodecs.decode(ConfiguredSpellSound.CODEC.codec(), tag);
        mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(castSlot);
        buf.writeNbt(ANCodecs.encode(ConfiguredSpellSound.CODEC.codec(), sound));
        buf.writeBoolean(mainHand);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof SpellBook) {
            SpellCaster caster = SpellCasterRegistry.from(stack);
            caster.setSound(sound, castSlot);
            Networking.sendToPlayerClient(new PacketUpdateBookGUI(stack), player);
            Networking.sendToPlayerClient(new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND), player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
